package com.sossbar.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.user.entity.UserLinkType;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProjectResponse {

    private Long projectId;
    private String projectName;
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    private String projectLink;
    private String projectImage;
    private ProjectStatus projectStatus;
    private List<ProjectMemberResponse> members;
    private int memberCount;
    private List<UserPosition> projectPositions;
    private String projectUrl;
    private UserLinkType projectUrlType;

    public static ProjectResponse from(
            Project project,
            List<ProjectMember> members,
            ProjectMember myMembership
    ) {

        List<ProjectMemberResponse> memberResponses =
                members.stream()
                        .map(ProjectMemberResponse::from)
                        .toList();

        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectLink(project.getProjectLink())
                .projectImage(project.getProjectImage())
                .projectStatus(project.getProjectStatus())
                .members(memberResponses)
                .memberCount(memberResponses.size())
                .projectPositions(
                        myMembership.getProjectPosition1() != null
                                ? myMembership.getProjectPositions()
                                : myMembership.getUser().getDefaultPositions()
                )
                .projectUrl(project.getProjectUrl())
                .projectUrlType(project.getProjectUrlType())
                .build();
    }
}
