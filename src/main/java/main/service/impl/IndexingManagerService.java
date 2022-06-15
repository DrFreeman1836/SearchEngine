package main.service.impl;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import main.config.ListOfProperties;
import main.dto.SiteStatistics;
import main.dto.Statistics;
import main.dto.Total;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.model.StatusType;
import main.repository.IndexRepository;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import main.service.ManagerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexingManagerService implements ManagerService {

  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final IndexRepository indexRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexingService indexingService;
  private final ForkJoinPool pool = new ForkJoinPool();
  private Set<Callable<Map<String, String>>> set = new HashSet<Callable<Map<String, String>>>();
  private List<Future<Map<String, String>>> futures = new ArrayList<>();

  public boolean startingIndexing(List<ListOfProperties.Site> listSites) {
    if (checkingIndexing()) {
      return false;
    }

    deleteAll();
    set.clear();
    futures.clear();

    for (ListOfProperties.Site site : listSites) {
      Site s = new Site();
      s.setUrl(site.getUrl());
      s.setName(site.getName());
      s.setStatus(StatusType.INDEXING);
      s.setStatusTime(new Date());
      addSite(s);

      set.add(new Callable<Map<String, String>>() {
        public Map<String, String> call() {
          return start(site);
        }
      });
    }

    futures = pool.invokeAll(set);

    for (Future<Map<String, String>> future : futures) {
      try {
        System.out.println(future.get().keySet());//
        updateStatusSite(future.get());
      } catch (InterruptedException | ExecutionException ex) {
        ex.printStackTrace();
      }
    }
    return true;
  }

  public boolean startingIndexing(ListOfProperties.Site site) {
    Optional<Site> optionalSite = siteRepository.findByUrl(site.getUrl());
    if (optionalSite.isEmpty()) {
      Site s = new Site();
      s.setUrl(site.getUrl());
      s.setName(site.getName());
      s.setStatus(StatusType.INDEXING);
      s.setStatusTime(new Date());
      addSite(s);
      start(site);
    } else {
      if (checkingIndexing(site.getUrl())) {
        return false;
      } else {
        int siteId = updateSite(optionalSite.get(), StatusType.INDEXING, new Date());
        delete(siteId);
        updateStatusSite(start(site));
      }
    }
    return true;
  }

  private Map<String, String> start(ListOfProperties.Site site) {
    PageCrawlingService map = new PageCrawlingService(pageRepository, siteRepository);
    int siteId = siteRepository.findByUrl(site.getUrl()).get().getId();
    map.setUrl(site.getUrl() + "/");
    map.setSiteId(siteId);
    String result = new ForkJoinPool().invoke(map);

    try {
      indexingService.indexingPages(siteId);
    } catch (Exception ex) {
      result = ex.getMessage();
      ex.printStackTrace();
    }

    if (result.equals("done")) {
      return Map.of(site.getUrl(), "");
    } else {
      return Map.of(site.getUrl(), result);
    }
  }

  public boolean stopIndexing() {
    if (!checkingIndexing()) {
      return false;
    } else {
      pool.shutdown();
      pool.shutdownNow();
      return true;
    }
  }

  public Statistics getStatistic() {
    List<SiteStatistics> listSites = new ArrayList<>();
    for (Site site : siteRepository.findAll()) {
      listSites.add(SiteStatistics.builder()
          .url(site.getUrl())
          .name(site.getName())
          .status(site.getStatus())
          .error(site.getLastError())
          .pages(pageRepository.findAllBySiteId(site.getId()).size())
          .lemmas(lemmaRepository.findAllBySiteId(site.getId()).size()).build());
    }

    Total total = Total.builder()
        .sites(siteRepository.findAll().size())
        .pages(pageRepository.findAll().size())
        .lemmas(lemmaRepository.findAll().size())
        .isIndexing(checkingIndexing()).build();

    return Statistics.builder()
        .total(total)
        .detailed(listSites).build();
  }

  private void delete(int siteId) {
    List<Page> listPages = pageRepository.findAllBySiteId(siteId);
    List<Lemma> listLemmas = lemmaRepository.findAllBySiteId(siteId);
    List<Index> listIndex = new ArrayList<>();
    for (Page page : listPages) {
      listIndex.addAll(indexRepository.findAllByPage(page));
    }
    indexRepository.deleteAll(listIndex);
    pageRepository.deleteAll(listPages);
    lemmaRepository.deleteAll(listLemmas);
  }

  private boolean checkingIndexing() {
    return siteRepository.findAll().stream().map(Site::getStatus).toList()
        .contains(StatusType.INDEXING);
  }

  private boolean checkingIndexing(String siteUrl) {
    return siteRepository.findByUrl(siteUrl).get().getStatus().equals(StatusType.INDEXING);
  }

  @Override
  public void addPage(Page page) {

  }

  @Override
  public List<String> getPathPages() {
    return null;
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
    siteRepository.save(site);
  }

  @Override
  public int updateSite(Site site, StatusType statusType, Date date) {
    site.setStatus(statusType);
    site.setStatusTime(date);
    return siteRepository.save(site).getId();
  }

  @Override
  public void updateStatusSite(Map<String, String> listSites) {
    for (String key : listSites.keySet()) {
      Optional<Site> optionalSite = siteRepository.findByUrl(key);
      if (optionalSite.isPresent()) {
        if (listSites.get(key).equals("")) {
          optionalSite.get().setStatus(StatusType.INDEXED);
          siteRepository.save(optionalSite.get());
        } else {
          optionalSite.get().setStatus(StatusType.FAILED);
          optionalSite.get().setLastError(listSites.get(key));
          siteRepository.save(optionalSite.get());
        }
      } else {
        System.out.println("Сайт не найден");
      }
    }
  }

  @Override
  public void deleteAll() {
    indexRepository.deleteAll();
    pageRepository.deleteAll();
    lemmaRepository.deleteAll();
    siteRepository.deleteAll();
  }
}
