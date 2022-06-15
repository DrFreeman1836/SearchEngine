package main.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Total {

  private int sites;
  private int pages;
  private int lemmas;
  private boolean isIndexing;


}
