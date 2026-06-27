package com.sossbar.projects.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    // 팀원 초대
    @Transactional
    public void inviteProjectMember(Principal principal, Long projectId) {
        User user = getLoginUser(principal);
        Long loginUserId = user.getId();
        Project project = getProjectById(projectId);

        if(projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new BusinessException(
                    ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION,
                    ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .user(user)
                .memberStatus(MemberStatus.MEMBER)
                .projectPosition(null)
                .projectDetailPosition(null)
                .build();

        projectMemberRepository.save(projectMember);
    }

    // 팀원 삭제
    @Transactional
    public void deleteProjectMember(Principal principal, Long projectId, Long userId) {
        User loginUser = getLoginUser(principal);
        Long loginUserId = loginUser.getId();
        Project project = getProjectById(projectId);

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

        projectMemberRepository.delete(targetMember);
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
}
