package com.sossbar.projects.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.dto.request.ProjectCreateRequest;
import com.sossbar.projects.dto.request.ProjectUpdateRequest;
import com.sossbar.projects.dto.response.ProjectResponse;
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

        return toResponse(project);
    }

    public ProjectResponse getProject(Long projectId) {
        return toResponse(findProjectById(projectId));
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, String newImageUrl) {
        Project project = findProjectById(projectId);
        project.update(request.getProjectName(), request.getHost(),
                request.getStartDate(), request.getEndDate(), newImageUrl);
        return toResponse(project);
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

    // Project -> ProjectResponse 변환
    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .host(project.getHost())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectLink(project.getProjectLink())
                .projectImage(project.getProjectImage())
                .projectStatus(project.getProjectStatus())
                .build();
    }
}
