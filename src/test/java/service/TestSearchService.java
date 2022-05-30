package service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DatabaseTearDowns;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import main.Application;
import main.dto.DtoPage;
import main.model.Lemma;
import main.model.Page;
import main.repository.LemmaRepository;

import main.repository.PageRepository;
import main.service.impl.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestExecutionListeners({
    TransactionalTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})
@Transactional
@TestPropertySource(locations = "/application-test.properties")
public class TestSearchService {

  @Autowired
  private LemmaRepository lemmaRepository;

  @Autowired
  private PageRepository pageRepository;

  @Autowired
  SearchService service;

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/lemma-data.xml")})
  @DatabaseTearDowns({
      @DatabaseTearDown("/lemma-data.xml")})
  public void getListSortedLemmasTest() {
    List<Lemma> actual = service.getListSortedLemmas("магазин в владимире");
    Lemma lemma = lemmaRepository.getById(6);
    Lemma lemma2 = lemmaRepository.getById(1);
    List<Lemma> expected = new ArrayList<>(List.of(lemma, lemma2));
    assertEquals(expected.stream()
        .sorted(Comparator.comparingInt(Lemma::getFrequency)).toList(), actual);
  }

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/lemma-data.xml"),
      @DatabaseSetup("/page-data.xml"),
      @DatabaseSetup("/indexes-data.xml")})
  public void isLemmaOnPageTest() {
    Page page = pageRepository.getById(1);
    Lemma lemma = lemmaRepository.getById(1);
    Lemma lemma2 = lemmaRepository.getById(6);
    boolean actual = service.isLemmaOnPage(page, lemma);
    assertTrue(actual);
    actual = service.isLemmaOnPage(page, lemma2);
    assertFalse(actual);
  }

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/page-data.xml")})
  public void getTitleTest() {
    Page page = pageRepository.getById(3);
    String actual = service.getTitle(page);
    String expected = "Владимир";
    assertEquals(expected, actual);
  }

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/lemma-data.xml"),
      @DatabaseSetup("/page-data.xml"),
      @DatabaseSetup("/indexes-data.xml")})
  public void getAbsRelevanceTest() {
    Page page = pageRepository.getById(1);
    Page page2 = pageRepository.getById(2);
    Lemma lemma = lemmaRepository.getById(2);//интернет
    Lemma lemma2 = lemmaRepository.getById(1);//магазин
    List<Lemma> listLemmas = new ArrayList<>(List.of(lemma, lemma2));
    Float actual = service.getAbsRelevance(page, listLemmas);
    Float expected = 2.8F;
    assertEquals(expected, actual);

    listLemmas = new ArrayList<>(List.of(lemma2));
    actual = service.getAbsRelevance(page2, listLemmas);
    expected = 0.8F;
    assertEquals(expected, actual);
  }

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/lemma-data.xml"),
      @DatabaseSetup("/page-data.xml"),
      @DatabaseSetup("/indexes-data.xml")})
  public void getMaxRelevanceTest() {
    Page page = pageRepository.getById(1);
    Page page2 = pageRepository.getById(2);
    Lemma lemma = lemmaRepository.getById(1);//магазин
    List<Lemma> listLemmas = new ArrayList<>(List.of(lemma));
    List<Page> listPages = new ArrayList<>(List.of(page, page2));
    Float actual = service.getMaxRelevance(listPages, listLemmas);
    Float expected = 1.8F;
    assertEquals(expected, actual);
  }

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/lemma-data.xml"),
      @DatabaseSetup("/page-data.xml"),
      @DatabaseSetup("/indexes-data.xml")})
  public void getSnippetTest() {
    Page page = pageRepository.getById(1);
    Lemma lemma = lemmaRepository.getById(1);//магазин
    Lemma lemma2 = lemmaRepository.getById(6);//владимир
    List<Lemma> listLemmas = new ArrayList<>(List.of(lemma, lemma2));
    List<String> actual = service.getSnippet(page, listLemmas);
    List<String> expected = new ArrayList<>();
    expected.add("<b>" + "Интернет магазин" + "</b>");
    expected.add("<b>" + "Магазин планшетов" + "</b>");
    assertEquals(expected, actual);
  }

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/lemma-data.xml"),
      @DatabaseSetup("/page-data.xml"),
      @DatabaseSetup("/indexes-data.xml"),
      @DatabaseSetup("/site-data.xml")})
  public void getPagesOnRequestTest() {
    DtoPage dtoPage = DtoPage.builder()
        .title("Интернет магазин")
        .url("/")
        .site("1")
        .relevance(1F).build();//max - 1.8
    DtoPage dtoPage2 = DtoPage.builder()
        .title("Корзина")
        .url("/basket")
        .site("1")
        .relevance(0.4444F).build();//max - 1.8
    List<String> listDtoPageActual = service.getPagesByRequest("интернет магазин", null)
        .stream().map(DtoPage::getTitle).toList();
    List<DtoPage> dtoPageList = new ArrayList<>(List.of(dtoPage));
    List<String> expected = dtoPageList.stream().map(DtoPage::getTitle).toList();
    assertEquals(expected, listDtoPageActual);//без фильтрации по сайтам

    listDtoPageActual = service.getPagesByRequest("интернет магазин", "2")
        .stream().map(DtoPage::getTitle).toList();
    dtoPageList = new ArrayList<>(List.of());
    expected = dtoPageList.stream().map(DtoPage::getTitle).toList();
    assertEquals(expected, listDtoPageActual);//с фильтраций по сайтам

  }

}
