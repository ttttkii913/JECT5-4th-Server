package com.sossbar.projects.dto.response;

import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectMemberResponse {

    private Long projectMemberId;
    private Long userId;
    private String username;
    private String profileImageUrl;
    private MemberStatus memberStatus;

    @Builder.Default
    private boolean reviewWritten = false;

    public static ProjectMemberResponse from(ProjectMember pm) {
        return ProjectMemberResponse.builder()
                .projectMemberId(pm.getProjectMemberId())
                .userId(pm.getUser().getId())
                .username(pm.getUser().getUsername())
                .profileImageUrl(pm.getUser().getProfileImageUrl())
                .memberStatus(pm.getMemberStatus())
                .build();
    }

    public static ProjectMemberResponse from(
            ProjectMember pm,
            boolean reviewWritten
    ) {
        return ProjectMemberResponse.builder()
                .projectMemberId(pm.getProjectMemberId())
                .userId(pm.getUser().getId())
                .username(pm.getUser().getUsername())
                .profileImageUrl(pm.getUser().getProfileImageUrl())
                .memberStatus(pm.getMemberStatus())
                .reviewWritten(reviewWritten)
                .build();
    }
}
