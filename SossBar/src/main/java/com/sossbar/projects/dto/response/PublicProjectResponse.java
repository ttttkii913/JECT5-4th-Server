package com.sossbar.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.user.entity.UserLinkType;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PublicProjectResponse {

    private Long projectId;
    private String projectName;
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private String projectImage;

    private List<UserPosition> projectPositions;

    private String projectUrl;
    private UserLinkType projectUrlType;

    public static PublicProjectResponse from(ProjectMember pm) {

        Project project = pm.getProject();

        return PublicProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectImage(project.getProjectImage())
                .projectPositions(pm.getProjectPositions())
                .projectUrl(project.getProjectUrl())
                .projectUrlType(project.getProjectUrlType())
                .build();
    }
}
