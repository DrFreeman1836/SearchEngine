package main.model;

import com.sun.istack.NotNull;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {@Index(columnList = "path", name = "path_index")})
public class Page {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @NotNull
  @Column(name = "path")
  private String path;

  @NotNull
  @Column(name = "code")
  private int code;

  @Column(columnDefinition = "MEDIUMTEXT", name = "content")
  @NotNull
  private String content;

  @Column(name = "site_id")
  @NotNull
  private int siteId;

  @OneToMany(mappedBy = "page", fetch = FetchType.LAZY)
  private List<main.model.Index> listIndex;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Page page = (Page) o;
    return siteId == page.siteId && path.equals(page.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, siteId);
  }
}
