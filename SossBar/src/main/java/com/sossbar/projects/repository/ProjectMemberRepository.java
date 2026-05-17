package com.sossbar.projects.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("select pm from ProjectMember pm join fetch pm.user where pm.project = :project")
    List<ProjectMember> findAllByProject(@Param("project") Project project);

    @Modifying
    @Query("delete from ProjectMember pm where pm.project = :project")
    void deleteAllByProject(@Param("project") Project project);

    //중복 체크용
    boolean existsByProjectAndUser(Project project, User user);
}
