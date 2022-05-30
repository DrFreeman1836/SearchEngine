package repo;

import java.util.Optional;
import main.Application;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.repository.IndexRepository;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TestIndexRepository {

  @Autowired
  private IndexRepository indexRepository;

  @Autowired
  private PageRepository pageRepository;

  @Autowired
  private LemmaRepository lemmaRepository;

  Index index = new Index();
  Index index2 = new Index();
  Index index3 = new Index();
  Page page;
  Page page2;
  Lemma lemma;
  Lemma lemma2;


  @BeforeEach
  public void setUp() {
    page = Page.builder()
        .code(200)
        .content("")
        .path("/")
        .siteId(1)
        .build();
    pageRepository.save(page);
    page2 = Page.builder()
        .code(200)
        .content("")
        .path("/content")
        .siteId(1)
        .build();
    pageRepository.save(page2);

    lemma = Lemma.builder()
        .lemma("магазин")
        .frequency(21)
        .siteId(1).build();
    lemmaRepository.save(lemma);
    lemma2 = Lemma.builder()
        .lemma("корзина")
        .frequency(5)
        .siteId(1).build();
    lemmaRepository.save(lemma2);

    index.setPage(page);
    index.setLemma(lemma);
    index.setRank(1);
    indexRepository.save(index);

    index2.setPage(page);
    index2.setLemma(lemma2);
    indexRepository.save(index2);

    index3.setPage(page2);
    index3.setLemma(lemma);
    indexRepository.save(index3);
  }

  @AfterEach
  public void tearDown() {
    indexRepository.deleteAll();
    lemmaRepository.deleteAll();
    pageRepository.deleteAll();
  }

  @Test
  public void findAllByPage() {
    List<Index> indexes = indexRepository.findAllByPage(page);
    List<Index> expected = new ArrayList<>(List.of(index, index2));
    assertEquals(expected, indexes);
  }

  @Test
  public void findByLemmaAndPage() {
    Optional<Index> optionalIndexResult = indexRepository.findByLemmaAndPage(lemma, page);
    Index expected = index;
    assertEquals(expected, optionalIndexResult.get());
  }

}
