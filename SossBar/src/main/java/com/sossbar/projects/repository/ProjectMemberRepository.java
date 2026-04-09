package com.sossbar.projects.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Modifying
    @Query("delete from ProjectMember pm where pm.project = :project")
    void deleteAllByProject(@Param("project") Project project);
}
