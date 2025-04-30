package aiss.gitminer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import aiss.gitminer.model.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, String> {
}
