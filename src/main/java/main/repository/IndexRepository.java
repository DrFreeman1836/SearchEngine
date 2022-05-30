package main.repository;

import java.util.List;
import java.util.Optional;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

  Optional<Index> findByLemmaAndPage(Lemma lemma, Page Page);

  List<Index> findAllByPage(Page page);
}
