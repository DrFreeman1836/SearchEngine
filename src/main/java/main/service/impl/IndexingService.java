package main.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import main.model.Field;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.model.StatusType;
import main.repository.FieldRepository;
import main.repository.IndexRepository;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.service.Lemmatization;
import main.service.ManagerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexingService implements ManagerService {

  private final Lemmatization lemmatization;
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexRepository indexRepository;
  private final FieldRepository fieldRepository;

  @Autowired
  public IndexingService(Lemmatization lemmatization, PageRepository pageRepository,
      LemmaRepository lemmaRepository, IndexRepository indexRepository,
      FieldRepository fieldRepository) {
    this.lemmatization = lemmatization;
    this.indexRepository = indexRepository;
    this.fieldRepository = fieldRepository;
    this.pageRepository = pageRepository;
    this.lemmaRepository = lemmaRepository;
  }

  public void indexingPages(int siteId) throws Exception {
    List<Page> listPages = pageRepository.findAllBySiteId(siteId);
    List<Field> listFields = fieldRepository.findAll();
    for (Page page : listPages) {
      if (page.getCode() != 200 || page.getPath() == null || page.getContent() == null) {
        continue;
      }
      indexingPage(page, siteId, listFields);
    }
  }

  public void indexingPage(Page page, int siteId, List<Field> listFields) throws Exception {
    Document document = Jsoup.parse(page.getContent());
    for (Field field : listFields) {
      Elements contentQuery = document.select(field.getSelector());
      for (Element content : contentQuery) {
        String normalizeContent = content.text().replaceAll("[^ЁёА-я\s]", " ").trim();
        HashMap<String, Integer> lemmas = new HashMap<>(lemmatization.analysisText(normalizeContent));
        rankCalculation(page, lemmas, field.getWeight(), siteId);
      }
    }
  }

  public void rankCalculation(Page page, HashMap<String, Integer> lemmas, float weight, int siteId)
      throws Exception {
    lemmas.forEach((lemma, frequency) -> {
      Optional<Lemma> optionalLemma = lemmaRepository.findByLemmaAndSiteId(lemma, siteId);
      int idNewLemma;
      if (optionalLemma.isEmpty()) {
        idNewLemma = addLemma(lemma, frequency, siteId);
        Lemma lemmaRepositoryById = lemmaRepository.getById(idNewLemma);
        addIndex(page, lemmaRepositoryById, frequency * weight);

      } else {
        updateLemma(optionalLemma.get(), frequency);
        Optional<Index> optionalIndex = indexRepository.findByLemmaAndPage(optionalLemma.get(), page);
        if (optionalIndex.isEmpty()) {
          addIndex(page, optionalLemma.get(), frequency * weight);
        } else {
          updateIndex(optionalIndex.get(), frequency * weight);
        }
      }
    });
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
    return lemmaRepository.save(Lemma.builder()
        .lemma(lemma)
        .frequency(frequency)
        .siteId(siteId).build()).getId();
  }

  @Override
  public int updateLemma(Lemma lemma, int frequency) {
    lemma.setFrequency(lemma.getFrequency() + frequency);
    return lemmaRepository.save(lemma).getId();
  }

  @Override
  public void addIndex(Page page, Lemma lemma, float rank) {
    Index index = new Index();
    index.setPage(page);
    index.setLemma(lemma);
    index.setRank(rank);
    indexRepository.saveAndFlush(index);
  }

  @Override
  public void updateIndex(Index index, float rank) {
    index.setRank(index.getRank() + rank);
    indexRepository.save(index);
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