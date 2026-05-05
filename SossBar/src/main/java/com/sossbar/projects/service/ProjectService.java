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
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(Principal principal, ProjectCreateRequest request, String imageUrl) {
        // 1. 요청자 조회
        Long userId = Long.parseLong(principal.getName());
        User user = userRepository.findById(userId)
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));

        // 2. 내가 속한 ProjectMember 목록 조회 (fetch join으로 project 포함 → N+1 방지)
        List<ProjectMember> myMemberships = projectMemberRepository.findAllByUser(user);

        // 3. 각 Project의 전체 멤버 조회 후 나를 제외하고 MyProjectResponse로 변환
        return myMemberships.stream()
                .map(pm -> {
                    List<ProjectMember> otherMembers = projectMemberRepository.findAllByProject(pm.getProject())
                            .stream()
                            .filter(m -> !m.getUser().getId().equals(pm.getUser().getId()))
                            .toList();
                    return toMyResponse(pm, otherMembers);
                })
                .toList();
    }

    public List<PublicProjectResponse> getUserProjects(Long userId) {
        // 1. 조회 대상 User 조회
        User user = userRepository.findById(userId)
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

    // Delete: 추가적으로 정책 논의 필요.
    @Transactional
    public void deleteProject(Long projectId) {
        Project project = findProjectById(projectId);
        // ProjectMember 먼저 삭제 (FK 제약조건)
        projectMemberRepository.deleteAllByProject(project);
        projectRepository.delete(project);
    }

    // 공통 조회 메서드
    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
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
                .build();
    }

    // ProjectMember(나) + 나를 제외한 멤버 → MyProjectResponse 변환
    private MyProjectResponse toMyResponse(ProjectMember myMembership, List<ProjectMember> otherMembers) {
        Project project = myMembership.getProject();
        List<ProjectMemberResponse> memberResponses = toMemberResponses(otherMembers);

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
}
