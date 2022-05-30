package main.model;

import com.sun.istack.NotNull;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Site {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", name = "status")
  @NotNull
  private StatusType status;

  @Column(name = "status_time", columnDefinition = "DATETIME")
  @NotNull
  private Date statusTime;

  @Column(name = "last_error", columnDefinition = "TEXT")
  private String lastError;

  @Column(name = "url")
  @NotNull
  private String url;

  @Column(name = "name")
  @NotNull
  private String name;

}
