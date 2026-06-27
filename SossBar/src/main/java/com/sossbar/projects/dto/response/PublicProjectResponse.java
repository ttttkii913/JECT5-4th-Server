package com.sossbar.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PublicProjectResponse {

    private Long projectId;
    private String projectName;
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    private String projectImage;

    private UserPosition projectPosition;
    private String projectDetailPosition;

    public static PublicProjectResponse from(ProjectMember pm) {

        Project project = pm.getProject();

        return PublicProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getCreatedAt())
                .projectImage(project.getProjectImage())
                .projectPosition(pm.getProjectPosition())
                .projectDetailPosition(pm.getProjectDetailPosition())
                .build();
    }
}
