package aiss.gitminer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import aiss.gitminer.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
}