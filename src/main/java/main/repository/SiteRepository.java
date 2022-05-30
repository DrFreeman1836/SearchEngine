package main.repository;

import java.util.Optional;
import main.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

  Optional<Site> findByUrl(String url);
}
