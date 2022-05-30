package repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import main.Application;
import main.model.Lemma;
import main.repository.LemmaRepository;

import main.repository.SiteRepository;
import main.service.impl.SearchService;
import main.service.Lemmatization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TestLemmaRepository {

  @Autowired
  private LemmaRepository lemmaRepository;
  @Autowired
  private SiteRepository siteRepository;

  private Lemmatization lemmatization;

  private SearchService service;

  Lemma lemma;
  Lemma lemma2;
  Lemma lemma3;
  Lemma lemma4;


  @BeforeEach
  public void setUp() throws IOException {
    lemma = Lemma.builder()
        .lemma("магазин")
        .frequency(21)
        .siteId(1).build();
    lemma2 = Lemma.builder()
        .lemma("дорога")
        .frequency(5)
        .siteId(1).build();
    lemma3 = Lemma.builder()
        .lemma("магазин")
        .frequency(3)
        .siteId(2).build();
    lemma4 = Lemma.builder()
        .lemma("корзина")
        .frequency(1)
        .siteId(2).build();
    List<Lemma> listLemmas = new ArrayList<>(List.of(lemma, lemma2, lemma3, lemma4));
    lemmaRepository.saveAll(listLemmas);

    lemmatization = new Lemmatization();
    service = new SearchService(lemmatization, lemmaRepository, siteRepository);
  }

  @AfterEach
  public void tearDown() {
    lemmaRepository.deleteAll();
  }

  @Test
  @Rollback(value = false)
  public void findAllByLemmaTest() {
    List<Lemma> lemmas = lemmaRepository.findAllByLemma("магазин");
    List<Lemma> result = new ArrayList<>(List.of(lemma, lemma3));
    assertEquals(result, lemmas);
  }

  @Test
  @Rollback(value = false)
  public void findByLemmaAndSiteId() {
    Optional<Lemma> optionalLemma = lemmaRepository.findByLemmaAndSiteId("магазин", 2);
    assertEquals(optionalLemma.get(), lemma3);
  }

  @Test
  @Rollback(value = false)
  public void findAllBySiteId() {
    List<Lemma> lemmas = lemmaRepository.findAllBySiteId(2);
    List<Lemma> result = new ArrayList<>(List.of(lemma3, lemma4));
    assertEquals(result, lemmas);
  }

}
