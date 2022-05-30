package main.dto;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import main.model.StatusType;

@Getter
@Setter
@Builder
public class Statistics {

  private int sites;
  private int pages;
  private int lemmas;
  private boolean isIndexing;
  private List<SiteStatistics> detailed;


}
