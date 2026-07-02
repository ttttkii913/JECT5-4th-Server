package com.sossbar.projects.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.notification.entity.NotificationType;
import com.sossbar.notification.service.NotificationService;
import com.sossbar.projects.dto.request.ProjectPositionReqDto;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.repository.ReviewRepository;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserPosition;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReviewRepository reviewRepository;

    // 팀원 초대
    @Transactional
    public void inviteProjectMember(Principal principal, String projectLink, List<UserPosition> positions) {
        User user = getLoginUser(principal);
        Long loginUserId = user.getId();
        Project project = getProjectByLink(projectLink);

        ProjectMember projectMember =
                projectMemberRepository.findByProjectAndUser(project, user)
                        .orElse(null);

        if (projectMember != null) {
            if (projectMember.isBanned()) {
                throw new BusinessException(
                        ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION,
                        "한 번 추방된 프로젝트에는 다시 참여할 수 없습니다."
                );
            }

            throw new BusinessException(
                    ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION,
                    ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION.getMessage()
                            + " (projectLink: " + projectLink + ", userId: " + loginUserId + ")"
            );
        }

        projectMember = ProjectMember.builder()
                .project(project)
                .user(user)
                .memberStatus(MemberStatus.MEMBER)
                .projectPosition1(positions.get(0))
                .projectPosition2(positions.size() > 1 ? positions.get(1) : null)
                .build();

        projectMemberRepository.save(projectMember);

        // 팀원 합류 알림 발송
        List<User> existingMembers = projectMemberRepository.findAllByProject(project).stream()
                .map(ProjectMember::getUser)
                .filter(existingUser -> !existingUser.getId().equals(loginUserId)) // 본인 제외
                .toList();

        notificationService.sendNotification(
                existingMembers,
                NotificationType.PROJECT_JOINED,
                "새로운 팀원이 도착했어요",
                user.getUsername() + "님이 프로젝트 " + project.getProjectName() + "에 합류했어요."
        );

    }

    // 팀원 삭제
    @Transactional
    public void deleteProjectMember(Principal principal, Long projectId, Long userId) {
        User loginUser = getLoginUser(principal);
        Long loginUserId = loginUser.getId();
        Project project = getProjectById(projectId);

        // 팀 확정 이후에는 팀원 삭제 불가 = IN_PROGRESS 상태만 가능
        if (project.getProjectStatus() != ProjectStatus.IN_PROGRESS) {
            throw new BusinessException(
                    ErrorCode.INVALID_PROJECT_STATUS_EXCEPTION,
                    "팀이 확정된 프로젝트는 팀원을 내보낼 수 없습니다. (projectId: " + projectId + ")"
            );
        }

        ProjectMember projectMember = projectMemberRepository.findByProjectAndUser(project, loginUser)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")"));

        // 유저가 팀장인지 확인
        if(projectMember.getMemberStatus() != MemberStatus.LEADER) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED_MEMBER_DELETION_EXCEPTION,
                    ErrorCode.UNAUTHORIZED_MEMBER_DELETION_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")");
        }

        if(loginUserId.equals(userId)) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED_MEMBER_DELETION_EXCEPTION,
                    "팀장은 스스로를 삭제할 수 없습니다. (projectId: " + projectId + ", userId: " + loginUserId + ")");
        }

        User targetUser = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));

        ProjectMember targetMember = projectMemberRepository.findByProjectAndUser(project, targetUser)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + userId + ")"));

        targetMember.deleteMember();
    }

    // 팀원 확정
    @Transactional
    public void confirmProjectMembers(Principal principal, Long projectId) {
        User loginUser = getLoginUser(principal);
        Long loginUserId = loginUser.getId();
        Project project = getProjectById(projectId);

        ProjectMember loginMember = projectMemberRepository.findByProjectAndUser(project, loginUser)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")"));

        if(loginMember.getMemberStatus() != MemberStatus.LEADER) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED_MEMBER_CONFIRMATION_EXCEPTION,
                    ErrorCode.UNAUTHORIZED_MEMBER_CONFIRMATION_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")");
        }

        if(project.getProjectStatus() != ProjectStatus.IN_PROGRESS) {
            throw new BusinessException(
                    ErrorCode.INVALID_PROJECT_STATUS_EXCEPTION,
                    ErrorCode.INVALID_PROJECT_STATUS_EXCEPTION.getMessage() + "(projectId: " + projectId + ", currentStatus: " + project.getProjectStatus() + ")");
        }

        project.updateProjectStatus(ProjectStatus.COMPLETED);

        // 팀 확정 알림 발송
        List<User> members = projectMemberRepository.findAllByProject(project)
                .stream()
                .map(ProjectMember::getUser)
                .toList();

        notificationService.sendNotification(
                members,
                NotificationType.TEAM_COMPLETED,
                "팀이 확정되었어요",
                project.getProjectName() + " 후기를 작성해 보세요"
        );
        updateProjectStatus(project);
    }

    private void updateProjectStatus(Project project) {
        // 팀 확정 상태에서만 체크
        if (project.getProjectStatus() != ProjectStatus.COMPLETED) {
            return;
        }

        long memberCount = projectMemberRepository.countByProjectAndIsBannedFalse(project);
        long reviewCount = reviewRepository.countActiveMemberReviews(project);        long totalReviewCount = (long) memberCount * (memberCount - 1);

        if (reviewCount == totalReviewCount) {
            project.updateProjectStatus(ProjectStatus.ARCHIVED);
        }
    }

    // 공통 메소드
    private User getLoginUser(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));
    }

    private Project getProjectByLink(String projectLink) {
        return projectRepository.findActiveProjectByProjectLink(projectLink)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectLink));
    }
}
