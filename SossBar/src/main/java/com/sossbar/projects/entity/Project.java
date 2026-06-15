package com.sossbar.projects.entity;

import com.sossbar.global.common.template.BaseTimeEntity;
import com.sossbar.projects.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;          // 프로젝트 제목

    @Column(name = "host", nullable = false)
    private String host;                 // 주최사

    @Column(name = "project_link")
    private String projectLink;          // 프로젝트 링크 (랜덤 uuid)

    @Column(name = "project_image")
    private String projectImage;         // 이미지 (S3 url)

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus; // 프로젝트 진행 상황

    // 프로젝트 수정 메서드
    public void update(String projectName, String host, String projectImage) {
        if (projectName != null) this.projectName = projectName;
        if (host != null) this.host = host;
        if (projectImage != null) this.projectImage = projectImage;
    }

    // 프로젝트 상태 변경 메서드
    public void updateProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    // 프로젝트 상태 변경(삭제)
    public void deleteProject() {
        this.projectStatus = ProjectStatus.DELETED;
    }
}
