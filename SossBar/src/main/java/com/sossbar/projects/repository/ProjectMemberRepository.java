package com.sossbar.projects.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("select pm from ProjectMember pm join fetch pm.user where pm.project = :project")
    List<ProjectMember> findAllByProject(@Param("project") Project project);

    @Query("select pm from ProjectMember pm join fetch pm.project where pm.user = :user")
    List<ProjectMember> findAllByUser(@Param("user") User user);

    @Query("select pm from ProjectMember pm join fetch pm.user where pm.project in :projects")
    List<ProjectMember> findAllByProjects(@Param("projects") List<Project> projects);

    @Modifying
    @Query("delete from ProjectMember pm where pm.project = :project")
    void deleteAllByProject(@Param("project") Project project);

    //중복 체크용
    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
}
