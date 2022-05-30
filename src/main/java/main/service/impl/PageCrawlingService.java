package main.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.model.StatusType;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import main.service.ManagerService;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PageCrawlingService extends RecursiveTask<String> implements ManagerService {

  private String url;
  private int siteId;
  private final PageRepository pageRepository;
  private final SiteRepository siteRepository;

  @Value("${referrer}")
  private final String referrer = "";

  @Value("${useragent}")
  private final String userAgent = "";

  @Autowired
  public PageCrawlingService(PageRepository pageRepository, SiteRepository siteRepository) {
    this.pageRepository = pageRepository;
    this.siteRepository = siteRepository;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setSiteId(int siteId) {
    this.siteId = siteId;
  }

  @Async
  @Override
  protected String compute() {
    List<PageCrawlingService> listTask = new ArrayList<>();
    try {
      Thread.sleep(500);
      Response response = Jsoup.connect(url)
          .userAgent(userAgent)
          .referrer(referrer)
          .execute();
      Document document = response.parse();
      String content = document.select("html").toString();
      String path = getPath(url);
      int code = response.statusCode();
      Page page = Page.builder()
          .path(path)
          .code(code)
          .content(content)
          .siteId(siteId)
          .build();
      synchronized (pageRepository) {
        if (!isPageOnDataBase(page)) {
          try {
            addPage(page);
          } catch (JpaSystemException ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();
          }
        }
      }

      Elements elements = document.select("a");
      elements.forEach(element -> {
        String nextUrl = element.absUrl("href");
        if (!nextUrl.isEmpty() && nextUrl.startsWith(url)
            && !isPathOnPages(getPath(nextUrl), siteId)
            && !nextUrl.contains("#")) {
          PageCrawlingService mapWebSite = new PageCrawlingService(pageRepository, siteRepository);
          mapWebSite.setUrl(nextUrl);
          mapWebSite.setSiteId(siteId);
          mapWebSite.fork();
          listTask.add(mapWebSite);
        }
      });

    } catch (HttpStatusException ex) {
      System.out.println(ex.getStatusCode());
      addPage(Page.builder()
          .path(url.substring(url.indexOf(".ru") + 3))
          .code(ex.getStatusCode())
          .siteId(siteId)
          .build());
    } catch (UnsupportedMimeTypeException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    listTask.forEach(ForkJoinTask::join);

    return "done";
  }

  public boolean isPathOnPages(String path, int siteId) {
    return pageRepository.findByPathAndSiteId(path, siteId).isPresent();
  }

  public String getPath(String url) {
    return url.replaceAll("^(https?:\\/\\/)(www.)?([\\da-z\\.-]+)\\.([a-z]{2,6})", "");
  }

  public boolean isPageOnDataBase(Page page) {
    return pageRepository.findAll().contains(page);
  }

  @Override
  public void addPage(Page page) throws JpaSystemException {
    pageRepository.save(page);
  }

  @Override
  public List<String> getPathPages() {
    return pageRepository.findAll()
        .stream()
        .map(Page::getPath)
        .collect(Collectors.toList());
  }

  @Override
  public int addLemma(String lemma, int frequency, int siteId) {
    return 0;
  }

  @Override
  public int updateLemma(Lemma lemma, int frequency) {
    return 0;
  }

  @Override
  public void addIndex(Page page, Lemma lemma, float rank) {

  }

  @Override
  public void updateIndex(Index index, float rank) {

  }

  @Override
  public void addSite(Site site) {

  }

  @Override
  public int updateSite(Site site, StatusType statusType, Date date) {
    return 0;
  }

  @Override
  public void updateStatusSite(Map<String, String> listSites) {

  }

  @Override
  public void deleteAll() {

  }

}
