package main.model;

import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Field {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "selector")
  private String selector;

  @NotNull
  @Column(name = "weight")
  private float weight;

}
