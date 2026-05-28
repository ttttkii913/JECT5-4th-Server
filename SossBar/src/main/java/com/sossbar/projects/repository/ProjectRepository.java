package com.sossbar.projects.repository;

import com.sossbar.projects.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
    SELECT p
    FROM Project p
    WHERE p.projectId = :projectId
      AND p.projectStatus <> 'DELETED'
""")
    Optional<Project> findActiveProjectById(Long projectId);
}
