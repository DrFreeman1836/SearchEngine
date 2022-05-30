package main.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import main.dto.DtoPage;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.repository.LemmaRepository;
import main.repository.SiteRepository;
import main.service.Lemmatization;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final Lemmatization lemmatization;
  private final LemmaRepository lemmaRepository;
  private final SiteRepository siteRepository;

  public List<Lemma> getListSortedLemmas(String request) {
    List<Lemma> listLemOnRequest = new ArrayList<>();
    String[] arrayWordsRequest = request.split(" ");
    for (String word : arrayWordsRequest) {
      List<String> listLemmas = lemmatization.getLemmas(word);
      for (String lemma : listLemmas) {
        List<Lemma> lemmas = lemmaRepository.findAllByLemma(lemma);
        if (listLemmas.isEmpty()) {
          continue;
        }
        //
        listLemOnRequest.addAll(lemmas);
      }
    }
    return listLemOnRequest.stream().sorted(Comparator.comparingInt(Lemma::getFrequency)).toList();
  }

  public List<DtoPage> getPagesByRequest(String request) {
    List<Lemma> sortedListLemmas = getListSortedLemmas(request);
    List<DtoPage> listDtoPage = new ArrayList<>();
    if (sortedListLemmas.isEmpty()) {
      return listDtoPage;
    }
    List<Page> listPageOnRequest = new ArrayList<>(sortedListLemmas.get(0).getListIndex()
        .stream()
        .map(Index::getPage)
        .toList());

    listPageOnRequest.removeIf(page -> {
      for (Lemma lemma : sortedListLemmas) {
        if (!isLemmaOnPage(page, lemma)) {
          return true;
        }
      }
      return false;
    });

    float maxRelevance = getMaxRelevance(listPageOnRequest, sortedListLemmas);

    listPageOnRequest.forEach(page -> {
      float relevance = getAbsRelevance(page, sortedListLemmas) / maxRelevance;
      Site sitePage = siteRepository.getById(page.getSiteId());
      listDtoPage.add(DtoPage.builder()
          .site(sitePage.getUrl())
          .siteName(sitePage.getName())
          .url(page.getPath())
          .title(getTitle(page))
          .snippet(getSnippet(page, sortedListLemmas))
          .relevance(relevance).build());
    });

    return listDtoPage.stream()
        .sorted(Comparator.comparingDouble(DtoPage::getRelevance).reversed()).toList();
  }

  public List<DtoPage> getPagesByRequest(String request, String siteForSearch) {
    if(siteForSearch == null){
      return getPagesByRequest(request);
    }
    return getPagesByRequest(request).stream()
        .filter(page -> page.getSite().equals(siteForSearch)).toList();
  }

  public float getAbsRelevance(Page page, List<Lemma> lemmasRequest) {
    float absRelevance = 0;
    for (Index indexOfPage : page.getListIndex()) {
      if (lemmasRequest.contains(indexOfPage.getLemma())) {
        absRelevance += indexOfPage.getRank();
      }
    }
    return absRelevance;
  }

  public float getMaxRelevance(List<Page> listPages, List<Lemma> lemmasRequest) {
    float maxAbsRelevance = 0;
    for (Page page : listPages) {
      float absRelevance = getAbsRelevance(page, lemmasRequest);
      if (absRelevance > maxAbsRelevance) {
        maxAbsRelevance = absRelevance;
      }
    }
    return maxAbsRelevance;
  }

  public String getTitle(Page page) {
    Document document = Jsoup.parse(page.getContent());
    Elements elements = document.select("title");
    return elements.text();
  }

  public List<String> getSnippet(Page page, List<Lemma> lemmasRequest) {
    List<String> snippet = new ArrayList<>();
    Document document = Jsoup.parse(page.getContent());
    for (Lemma lemma : lemmasRequest) {
      String query = "*:containsOwn(" + lemma.getLemma() + ")";
      Elements elements = document.select(query);
      elements.forEach(element -> {
        snippet.add("<b>" + element.text() + "</b>");
      });
    }
    return snippet;
  }

  public boolean isLemmaOnPage(Page page, Lemma lemma) {
    List<Lemma> listLemmas = page.getListIndex().stream()
        .map(Index::getLemma)
        .toList();
    return listLemmas.contains(lemma);
  }
}
