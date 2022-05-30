package repo;

import java.util.Optional;
import main.Application;
import main.model.Lemma;
import main.model.Page;
import main.repository.PageRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TestPageRepository {

  @Autowired
  private PageRepository pageRepository;

  Page page;
  Page page2;
  Page page3;

  @BeforeEach
  public void setUp() {
    page = Page.builder()
        .code(200)
        .content("")
        .path("/")
        .siteId(1)
        .build();
    page2 = Page.builder()
        .code(200)
        .content("")
        .path("/content")
        .siteId(1)
        .build();
    page3 = Page.builder()
        .code(200)
        .content("")
        .path("/content")
        .siteId(2)
        .build();

    List<Page> listPages = new ArrayList<>(List.of(page, page2, page3));
    pageRepository.saveAll(listPages);
  }

  @AfterEach
  public void tearDown() {
    pageRepository.deleteAll();
  }

  @Test
  @Rollback(value = false)
  public void findAllBySiteIdTest() {
    List<Page> pages = pageRepository.findAllBySiteId(1);
    List<Page> result = new ArrayList<>(List.of(page, page2));
    assertEquals(pages, result);
  }

  @Test
  @Rollback(value = false)
  public void findByPathAndSiteIdTest() {
    Optional<Page> result = pageRepository.findByPathAndSiteId("/content", 2);
    assertEquals(page3, result.get());
  }


}