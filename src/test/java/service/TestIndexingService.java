package service;

import static org.junit.Assert.assertEquals;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DatabaseTearDowns;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.annotation.ExpectedDatabases;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import main.Application;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.repository.FieldRepository;
import main.repository.IndexRepository;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.service.impl.IndexingService;
import main.service.impl.SearchService;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;
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
public class TestIndexingService {

  @Autowired
  private LemmaRepository lemmaRepository;

  @Autowired
  private IndexRepository indexRepository;

  @Autowired
  private IndexingService service;

  @Test
  @DatabaseSetups({
      @DatabaseSetup("/page-data.xml"),
      @DatabaseSetup("/field-data.xml")})
  @ExpectedDatabase(assertionMode = DatabaseAssertionMode.NON_STRICT, value = "/lemma-data.xml")
  public void indexingPagesTest() throws Exception {
    service.indexingPages(1);
    service.indexingPages(2);
    for (Lemma lemma : lemmaRepository.findAll()) {
      System.out.println(lemma.getId() + " " + lemma.getLemma() + " " + lemma.getFrequency() + " "
          + lemma.getSiteId());
    }
    for (Index index : indexRepository.findAll()) {
      System.out.println(index.getId() + " " + index.getLemma().getLemma() + " " + index.getRank() + " " + index.getLemma().getId() + " " + index.getPage().getId());
    }

    assertEquals(9, indexRepository.findAll().size());

    double actual = indexRepository.getById(1).getRank();
    double expected = 1.8;
    assertEquals(expected, actual, 0.05);
  }


}