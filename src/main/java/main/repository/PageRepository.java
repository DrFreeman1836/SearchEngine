package main.repository;

import java.util.List;
import java.util.Optional;
import main.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
  Optional<Page> findByPathAndSiteId(String path, int siteId);

  List<Page> findAllBySiteId(int siteId);
}
