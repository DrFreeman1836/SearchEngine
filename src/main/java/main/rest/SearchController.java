package main.rest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import main.config.ListOfProperties;
import lombok.RequiredArgsConstructor;
import main.config.ListOfProperties.Site;
import main.dto.DtoPage;
import main.dto.DtoResponse;
import main.dto.Statistics;
import main.dto.Total;
import main.service.impl.IndexingManagerService;
import main.service.impl.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

  private final IndexingManagerService indexingManagerService;
  private final SearchService searchService;
  private final ListOfProperties listOfProperties;

  @GetMapping("/startIndexing")
  public ResponseEntity<?> startIndexing() {
    if (indexingManagerService.startingIndexing(listOfProperties.getSites())) {
      return ResponseEntity.ok().body(Map.of("result", true));
    }
    return ResponseEntity.status(405)
        .body(Map.of("result", false, "error", "Индексация уже запущена"));
  }

  @GetMapping("/stopIndexing")
  public ResponseEntity<?> stopIndexing() {
    if (indexingManagerService.stopIndexing()) {
      return ResponseEntity.ok().body(Map.of("result", true));
    }
    return ResponseEntity.status(405).body(Map.of("result", false, "error", "Индексация не запущена"));
  }

  @PostMapping("/indexPage")
  public ResponseEntity<?> addPage(@RequestParam String url) {
    Optional<Site> siteConfig = listOfProperties.getSites().stream()
        .filter(site -> site.getUrl().equals(url)).findAny();
    if (siteConfig.isEmpty()) {
      return ResponseEntity.status(405).body(Map.of("result", false,
          "error","Данная страница находится за пределами сайтов, указанных в конфигурационном файле"));
    }
    if (!indexingManagerService.startingIndexing(siteConfig.get())) {
      return ResponseEntity.status(405).body(Map.of("result", false,
          "error","Индексация даннной страницы уже запущена"));
    } else {
      return ResponseEntity.ok().body(Map.of("result", true));
    }
  }

  @GetMapping("/statistics")
  public ResponseEntity<?> getStatistics() {
    Statistics statistics = indexingManagerService.getStatistic();
    return ResponseEntity.ok().body(Map.of("result", true, "statistics", statistics));
  }

  @GetMapping("/search")
  public ResponseEntity<?> search(
      @RequestParam(name = "query", required = false) String query,
      @RequestParam(name = "site", required = false) String site,
      @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
      @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {

    if (query == null) {
      return ResponseEntity.status(405)
          .body(Map.of("result", false, "error", "Задан пустой поисковой запрос"));
    }

    List<DtoPage> listPageByRequest;
    listPageByRequest = searchService.getPagesByRequest(query, site).stream()
        .skip(offset).limit(limit).toList();

    DtoResponse response = DtoResponse.builder()
        .result(true)
        .count(listPageByRequest.size())
        .data(listPageByRequest)
        .build();
    return ResponseEntity.ok().body(response);
  }

}
/**
 * доработать метод старта статистики и остоновки
 * выделить жирным
 * на хероку
 */