package com.sossbar.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class MyProjectResponse {

    private Long projectId;
    private String projectName;
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    private String projectLink;
    private String projectImage;
    private ProjectStatus projectStatus;
    private MemberStatus myMemberStatus;
    private List<ProjectMemberResponse> members;
    private int memberCount;
    private UserPosition projectPosition;
    private String projectDetailPosition;

    public static MyProjectResponse from(
            ProjectMember myMembership,
            List<ProjectMember> otherMembers,
            Set<Long> reviewedUserIds,
            int memberCount
    ) {

        Project project = myMembership.getProject();

        List<ProjectMemberResponse> members =
                otherMembers.stream()
                        .map(pm -> ProjectMemberResponse.from(
                                pm,
                                reviewedUserIds.contains(pm.getUser().getId())
                        ))
                        .toList();

        return MyProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getCreatedAt())
                .projectLink(project.getProjectLink())
                .projectImage(project.getProjectImage())
                .projectStatus(project.getProjectStatus())
                .myMemberStatus(myMembership.getMemberStatus())
                .projectPosition(myMembership.getProjectPosition())
                .projectDetailPosition(myMembership.getProjectDetailPosition())
                .members(members)
                .memberCount(memberCount)
                .build();
    }
}
