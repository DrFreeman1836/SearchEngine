package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import main.Application;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.service.impl.PageCrawlingService;
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
public class TestPageCrawlingService {

  @Autowired
  PageCrawlingService service;

  @Test
  public void getPathTest() {
    String result = service.getPath(
        "https://ipfran.ru/scientific-activity/important-result/20220228-akkrecia-in-stars");
    String expected = "/scientific-activity/important-result/20220228-akkrecia-in-stars";
    assertEquals(expected, result);

    result = service.getPath("https://ipfran.ru/");
    expected = "/";
    assertEquals(expected, result);

    result = service.getPath("https://www.volochek.life/");
    expected = "/";
    assertEquals(expected, result);
  }

  @Test
  @DatabaseSetup("/page-data.xml")
  public void isPathOnPagesTest() {
    boolean result = service.isPathOnPages("/", 1);
    assertTrue(result);
    result = service.isPathOnPages("/basket", 2);
    assertFalse(result);
  }

  @Test
  @DatabaseSetup("/page-data.xml")
  public void isPageOnDataBaseTest() {
    Page page = Page.builder()
        .code(200)
        .path("/")
        .siteId(1)
        .build();
    boolean result = service.isPageOnDataBase(page);
    assertTrue(result);
    Page page2 = Page.builder()
        .code(200)
        .path("/basket")
        .siteId(2)
        .build();
    result = service.isPageOnDataBase(page2);
    assertFalse(result);
  }


}
