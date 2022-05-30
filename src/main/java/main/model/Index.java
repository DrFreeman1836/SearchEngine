package main.model;

import com.sun.istack.NotNull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "indexes")
@Getter
@Setter
public class Index {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @NotNull
  @org.hibernate.annotations.Index(name = "index1")
  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "page_id")
  private Page page;

  @NotNull
  @org.hibernate.annotations.Index(name = "index1")
  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "lemma_id")
  private Lemma lemma;

  @NotNull
  @Column(name = "raking")
  private float rank;

}
