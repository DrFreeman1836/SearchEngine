package main.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "listsites")
@Getter
@Setter
public class ListOfProperties {

  private List<Site> sites;

  public ListOfProperties(List<Site> sites) {
    this.sites = sites;
  }


  @Getter
  @Setter
  public static class Site {

    private String name;
    private String url;

  }

}
