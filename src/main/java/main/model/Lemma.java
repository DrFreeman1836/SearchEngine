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
import org.hibernate.annotations.ColumnDefault;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {@Index(columnList = "lemma", name = "lemma_index")})
public class Lemma {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @NotNull
  @Column(name = "lemma")
  private String lemma;

  @NotNull
  @ColumnDefault("1")
  @Column(name = "frequency")
  private int frequency;

  @Column(name = "site_id")
  @NotNull
  private int siteId;

  @OneToMany(mappedBy = "lemma", fetch = FetchType.LAZY)
  private List<main.model.Index> listIndex;

//  @Override
//  public boolean equals(Object o) {
//    if (this == o) {
//      return true;
//    }
//    if (o == null || getClass() != o.getClass()) {
//      return false;
//    }
//    Lemma lemma1 = (Lemma) o;
//    return siteId == lemma1.siteId && lemma.equals(lemma1.lemma);
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hash(lemma, siteId);
//  }
}
