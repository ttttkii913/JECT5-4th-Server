package com.sossbar.projects.dto.response;

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
}
