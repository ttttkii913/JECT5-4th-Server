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

    @Column(name = "start_date")
    private LocalDateTime startDate;     // 프로젝트 시작 날짜

    @Column(name = "end_date")
    private LocalDateTime endDate;       // 프로젝트 종료 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus; // 프로젝트 진행 상황

    // 프로젝트 수정 메서드
    public void update(String projectName, String host, LocalDateTime startDate, LocalDateTime endDate, String projectImage) {
        if (projectName != null) this.projectName = projectName;
        if (host != null) this.host = host;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
        if (projectImage != null) this.projectImage = projectImage;
    }

    // 프로젝트 상태 변경 메서드
    public void updateProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }
}
