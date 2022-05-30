package main.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DtoPage {

  private String site;

  private String siteName;

  private String url;

  private String title;

  private List<String> snippet;

  private float relevance;

}
