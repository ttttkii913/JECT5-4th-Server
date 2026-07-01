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

    @Query("select pm from ProjectMember pm join fetch pm.user u where pm.project = :project and u.isDeleted = false and pm.isBanned = false")
    List<ProjectMember> findAllByProject(@Param("project") Project project);

    @Query("select pm from ProjectMember pm join fetch pm.project where pm.user = :user  and pm.user.isDeleted = false and pm.isBanned = false")
    List<ProjectMember> findAllByUser(@Param("user") User user);

    @Query("select pm from ProjectMember pm join fetch pm.user u where pm.project in :projects and u.isDeleted = false and pm.isBanned = false")
    List<ProjectMember> findAllByProjects(@Param("projects") List<Project> projects);

    @Query("select pm from ProjectMember pm join fetch pm.project join fetch pm.user where pm.project in :projects and pm.user in :users")
    List<ProjectMember> findAllByProjectInAndUserIn(List<Project> projects, List<User> users);

    @Modifying
    @Query("delete from ProjectMember pm where pm.project = :project")
    void deleteAllByProject(@Param("project") Project project);

    //중복 체크용
    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    Optional<ProjectMember> findFirstByProjectAndUser_IdNotAndUser_IsDeletedFalseOrderByCreatedAtAsc(Project project, Long userId);

    long countByProjectAndIsBannedFalse(Project project);
}
