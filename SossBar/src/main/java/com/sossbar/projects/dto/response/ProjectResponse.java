package com.sossbar.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.ProjectStatus;
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
    private String projectLink;
    private String projectImage;
    private ProjectStatus projectStatus;
    private List<ProjectMemberResponse> members;
    private int memberCount;
    private UserPosition projectPosition;
    private String projectDetailPosition;

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
                .startDate(project.getCreatedAt())
                .projectLink(project.getProjectLink())
                .projectImage(project.getProjectImage())
                .projectStatus(project.getProjectStatus())
                .members(memberResponses)
                .memberCount(memberResponses.size())
                .projectPosition(
                        myMembership.getProjectPosition() != null
                                ? myMembership.getProjectPosition()
                                : myMembership.getUser().getDefaultPosition()
                )
                .projectDetailPosition(
                        myMembership.getProjectDetailPosition() != null
                                ? myMembership.getProjectDetailPosition()
                                : myMembership.getUser().getDefaultDetailPosition()
                )
                .build();
    }
}
