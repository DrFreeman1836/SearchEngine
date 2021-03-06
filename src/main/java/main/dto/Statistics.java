package main.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Statistics {

  private Total total;
  private List<SiteStatistics> detailed;

}
