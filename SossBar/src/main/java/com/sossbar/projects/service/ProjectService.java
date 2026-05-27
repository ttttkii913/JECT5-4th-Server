package com.sossbar.projects.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.dto.request.ProjectCreateRequest;
import com.sossbar.projects.dto.request.ProjectUpdateRequest;
import com.sossbar.projects.dto.response.MyProjectResponse;
import com.sossbar.projects.dto.response.ProjectMemberResponse;
import com.sossbar.projects.dto.response.ProjectResponse;
import com.sossbar.projects.dto.response.PublicProjectResponse;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.repository.ReviewRepository;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ProjectResponse createProject(Principal principal, ProjectCreateRequest request, String imageUrl) {
        // 1. 요청자 조회
        Long userId = Long.parseLong(principal.getName());
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));

        // 2. 프로젝트 저장
        String projectLink = UUID.randomUUID().toString();
        Project project = Project.builder()
                .projectName(request.getProjectName())
                .host(request.getHost())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .projectLink(projectLink)
                .projectImage(imageUrl)
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .build();
        projectRepository.save(project);

        // 3. 생성자를 LEADER로 ProjectMember에 저장
        ProjectMember projectMember = ProjectMember.builder()
                .user(user)
                .project(project)
                .memberStatus(MemberStatus.LEADER)
                .build();
        projectMemberRepository.save(projectMember);

        return toResponse(project, List.of(projectMember));
    }

    public List<MyProjectResponse> getMyProjects(Principal principal) {
        // 1. principal로 userId 추출 → User 조회
        Long userId = Long.parseLong(principal.getName());
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));

        // 2. 내가 속한 ProjectMember 목록 조회 (fetch join으로 project 포함 → N+1 방지)
        List<ProjectMember> myMemberships = projectMemberRepository.findAllByUser(user);
        List<Project> myProjects = myMemberships.stream()
                .map(ProjectMember::getProject)
                .toList();

        List<ProjectMember> allMembers = projectMemberRepository.findAllByProjects(myProjects);

        Map<Long, List<ProjectMember>> membersByProject = allMembers.stream()
                .collect(Collectors.groupingBy(pm -> pm.getProject().getProjectId()));

        Map<Long, Set<Long>> reviewedUserIdsByProject = myProjects.stream()
                .collect(Collectors.toMap(
                        Project::getProjectId,
                        project -> reviewRepository.findRevieweeIdsByReviewerIdAndProjectId(userId, project.getProjectId())
                ));

        // 3. 각 Project의 전체 멤버 조회 후 나를 제외하고 MyProjectResponse로 변환
        return myMemberships.stream()
                .map(pm -> {
                    List<ProjectMember> allProjectMembers = membersByProject
                            .getOrDefault(pm.getProject().getProjectId(), List.of());
                    List<ProjectMember> otherMembers = allProjectMembers
                            .stream()
                            .filter(m -> !m.getUser().getId().equals(userId))
                            .toList();
                    Set<Long> reviewedUserIds = reviewedUserIdsByProject.getOrDefault(pm.getProject().getProjectId(), Set.of());
                    return toMyResponse(pm, otherMembers, reviewedUserIds, allProjectMembers.size());
                })
                .toList();
    }

    public List<PublicProjectResponse> getUserProjects(Long userId) {
        // 1. 조회 대상 User 조회
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));

        // 2. 해당 유저가 속한 ProjectMember 목록 조회 (fetch join으로 project 포함)
        List<ProjectMember> memberships = projectMemberRepository.findAllByUser(user);

        // 3. 각 Project의 전체 멤버 조회 후 PublicProjectResponse로 변환
        // TODO: 후기 작성이 완료된 프로젝트만 외부에 노출할 경우 아래 filter 활성화
        // 후기 작성 완료 상태 = ProjectStatus.ARCHIVED
        // .filter(pm -> pm.getProject().getProjectStatus() == ProjectStatus.ARCHIVED)

        return memberships.stream()
                .map(pm -> toPublicResponse(pm.getProject()))
                .toList();
    }

    public ProjectResponse getProject(Long projectId) {
        Project project = findProjectById(projectId);
        List<ProjectMember> members = projectMemberRepository.findAllByProject(project);
        return toResponse(project, members);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, String newImageUrl) {
        Project project = findProjectById(projectId);
        project.update(request.getProjectName(), request.getHost(),
                request.getStartDate(), request.getEndDate(), newImageUrl);
        List<ProjectMember> members = projectMemberRepository.findAllByProject(project);
        return toResponse(project, members);
    }

    public String getProjectImageUrl(Long projectId) {
        return findProjectById(projectId).getProjectImage();
    }

    // Delete: 추가적으로 정책 논의 필요. -> soft deleted로 '삭제' 상태로 변경
    @Transactional
    public void deleteProject(Long projectId) {
        Project project = findProjectById(projectId);
        // 프로젝트 상태 변경
        project.deleteProject();
        projectMemberRepository.deleteAllByProject(project);
    }

    // 공통 조회 메서드
    private Project findProjectById(Long projectId) {
        return projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));
    }

    // Project → ProjectResponse 변환
    private ProjectResponse toResponse(Project project, List<ProjectMember> members) {
        List<ProjectMemberResponse> memberResponses = toMemberResponses(members);

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
                .build();
    }

    // ProjectMember(나) + 나를 제외한 멤버 → MyProjectResponse 변환
    private MyProjectResponse toMyResponse(ProjectMember myMembership, List<ProjectMember> otherMembers, Set<Long> reviewedUserIds, int memberCount) {
        Project project = myMembership.getProject();
        List<ProjectMemberResponse> memberResponses = toMyMemberResponses(otherMembers, reviewedUserIds);

        return MyProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectLink(project.getProjectLink())
                .projectImage(project.getProjectImage())
                .projectStatus(project.getProjectStatus())
                .myMemberStatus(myMembership.getMemberStatus())
                .members(memberResponses)
                .memberCount(memberCount)
                .build();
    }

    // Project → PublicProjectResponse 변환
    private PublicProjectResponse toPublicResponse(Project project) {
        return PublicProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectImage(project.getProjectImage())
                .build();
    }

    // ProjectMember 리스트 → ProjectMemberResponse 리스트 변환 (공통)
    private List<ProjectMemberResponse> toMemberResponses(List<ProjectMember> members) {
        return members.stream()
                .map(pm -> ProjectMemberResponse.builder()
                        .projectMemberId(pm.getProjectMemberId())
                        .userId(pm.getUser().getId())
                        .username(pm.getUser().getUsername())
                        .profileImageUrl(pm.getUser().getProfileImageUrl())
                        .memberStatus(pm.getMemberStatus())
                        .build())
                .toList();
    }

    private List<ProjectMemberResponse> toMyMemberResponses(List<ProjectMember> members, Set<Long> reviewedUserIds) {
        return members.stream()
                .map(pm -> ProjectMemberResponse.builder()
                        .projectMemberId(pm.getProjectMemberId())
                        .userId(pm.getUser().getId())
                        .username(pm.getUser().getUsername())
                        .profileImageUrl(pm.getUser().getProfileImageUrl())
                        .memberStatus(pm.getMemberStatus())
                        .reviewWritten(reviewedUserIds.contains(pm.getUser().getId()))
                        .build())
                .toList();
    }
    
    @Transactional
    public void inviteProjectMember(Principal principal, Long projectId) {
        Long loginUserId = Long.parseLong(principal.getName());
        User user = userRepository.findByIdAndIsDeletedFalse(loginUserId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + loginUserId));

        Project project = projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));

        if(projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new BusinessException(
                    ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION,
                    ErrorCode.PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .user(user)
                .memberStatus(MemberStatus.MEMBER)
                .build();

        projectMemberRepository.save(projectMember);
    }

    @Transactional
    public void deleteProjectMember(Principal principal, Long projectId, Long userId) {
        // 유저가 팀장인지 확인
        Long loginUserId = Long.parseLong(principal.getName());

        Project project = projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));

        User loginUser = userRepository.findByIdAndIsDeletedFalse(loginUserId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + loginUserId));

        ProjectMember projectMember = projectMemberRepository.findByProjectAndUser(project, loginUser)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + loginUserId + ")"));

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

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));

        ProjectMember targetMember = projectMemberRepository.findByProjectAndUser(project, targetUser)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage() + " (projectId: " + projectId + ", userId: " + userId + ")"));

        projectMemberRepository.delete(targetMember);
    }

    @Transactional
    public void confirmProjectMembers(Principal principal, Long projectId) {
        Long loginUserId = Long.parseLong(principal.getName());

        Project project = projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));

        User loginUser = userRepository.findByIdAndIsDeletedFalse(loginUserId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + loginUserId));

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
                    ErrorCode.UNAUTHORIZED_MEMBER_CONFIRMATION_EXCEPTION.getMessage() + "(projectId: " + projectId + ", currentStatus: " + project.getProjectStatus() + ")");
        }

        project.updateProjectStatus(ProjectStatus.COMPLETED);
    }
}
