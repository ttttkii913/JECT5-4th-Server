package com.sossbar.projects.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    void deleteAllByProject(Project project);
}
