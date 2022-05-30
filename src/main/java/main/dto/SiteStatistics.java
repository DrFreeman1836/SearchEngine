package main.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import main.model.StatusType;

@Getter
@Setter
@Builder
public class SiteStatistics {

  private String url;
  private String name;
  private StatusType status;
  private Date statusTime;
  private String error;
  private int pages;
  private int lemmas;

}
