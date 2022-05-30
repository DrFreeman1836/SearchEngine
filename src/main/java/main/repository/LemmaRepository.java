package main.repository;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import main.model.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO lemma (lemma) VALUES (?) ON DUPLICATE KEY UPDATE frequency = lemma.frequency + 1", nativeQuery = true)
  void updateOnDuplicate(@Param("lemma") String lemma);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO lemma (lemma, frequency, site_id) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE frequency = lemma.frequency + VALUES(frequency)", nativeQuery = true)
  void updateOnDuplicate(@Param("lemma") String lemma, @Param("frequency") int frequency, @Param("site_id") int siteId);

  List<Lemma> findAllByLemma(String lemma);

  Optional<Lemma> findByLemmaAndSiteId(String lemma, int siteId);

  List<Lemma> findAllBySiteId(int siteId);
}
