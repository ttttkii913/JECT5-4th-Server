package com.sossbar.projects.entity;

import com.sossbar.global.common.template.BaseTimeEntity;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserPosition;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long projectMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false)
    private MemberStatus memberStatus;   // 리더 / 멤버 구분

    @Enumerated(EnumType.STRING)
    private UserPosition projectPosition1;
    @Enumerated(EnumType.STRING)
    private UserPosition projectPosition2;

    // 팀장 탈퇴시 권한 팀원에 위임
    public void changeMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    // 후기 작성시 디폴트 직군 변경 가능
    public void updateProjectPosition(List<UserPosition> positions) {
        this.projectPosition1 = (positions != null && !positions.isEmpty()) ? positions.get(0) : null;
        this.projectPosition2 = (positions != null && positions.size() > 1) ? positions.get(1) : null;
    }

    // 직군 조회 메소드
    public List<UserPosition> getProjectPositions() {
        List<UserPosition> positions = new ArrayList<>();

        if (projectPosition1 != null) {
            positions.add(projectPosition1);
        }

        if (projectPosition2 != null) {
            positions.add(projectPosition2);
        }

        return positions;
    }
}
