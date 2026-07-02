package com.sossbar.projects.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.dto.request.ProjectCreateRequest;
import com.sossbar.projects.dto.request.ProjectUpdateRequest;
import com.sossbar.projects.dto.response.MyProjectResponse;
import com.sossbar.projects.dto.response.ProjectResponse;
import com.sossbar.projects.dto.response.PublicProjectResponse;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.projects.enums.ProjectFilterType;
import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.projects.enums.SortType;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.repository.ReviewRepository;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserPosition;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
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

    // 프로젝트 생성
    @Transactional
    public ProjectResponse createProject(Principal principal, ProjectCreateRequest request, String imageUrl) {
        // 1. 요청자 조회
        User user = getLoginUser(principal);

        // 2. 시작, 종료 기간 검증
        validateProjectPeriod(request.getStartDate(), request.getEndDate());

        // 3. 프로젝트 저장
        String projectLink = UUID.randomUUID().toString();
        Project project = Project.builder()
                .projectName(request.getProjectName())
                .host(request.getHost())
                .projectLink(projectLink)
                .projectImage(imageUrl)
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .projectUrl(request.getProjectUrl())
                .projectUrlType(request.getProjectUrlType())
                .build();
        projectRepository.save(project);

        List<UserPosition> positions = request.getProjectPositions();

        // 4. 생성자를 LEADER로 ProjectMember에 저장
        ProjectMember projectMember = ProjectMember.builder()
                .user(user)
                .project(project)
                .memberStatus(MemberStatus.LEADER)
                .projectPosition1(positions.get(0))
                .projectPosition2(positions.size() > 1 ? positions.get(1) : null)
                .build();
        projectMemberRepository.save(projectMember);

        return ProjectResponse.from(project, List.of(projectMember), projectMember);
    }

    // 내 프로젝트 리스트 조회
    public List<MyProjectResponse> getMyProjects(Principal principal, SortType sort, ProjectFilterType status) {
        // 1. principal로 userId 추출 → User 조회
        User user = getLoginUser(principal);
        Long userId = user.getId();

        Sort sortOption =
                sort == SortType.LATEST
                        ? Sort.by(Sort.Direction.DESC, "project.createdAt")
                        : Sort.by(Sort.Direction.ASC, "project.createdAt");

        // 2. 내가 속한 ProjectMember 목록 조회 (fetch join으로 project 포함 → N+1 방지)
        List<ProjectMember> myMemberships = projectMemberRepository.findAllByUser(user, sortOption);

        // DELETED 제외
        List<ProjectMember> filteredMemberships = myMemberships.stream()
                .filter(pm -> pm.getProject().getProjectStatus() != ProjectStatus.DELETED)
                .filter(pm -> switch (status) {
                    case ALL -> true;
                    case IN_PROGRESS ->
                            pm.getProject().getProjectStatus() == ProjectStatus.IN_PROGRESS;
                    case COMPLETED ->
                            pm.getProject().getProjectStatus() == ProjectStatus.COMPLETED
                                    || pm.getProject().getProjectStatus() == ProjectStatus.ARCHIVED;
                })
                .toList();

        List<Project> myProjects = filteredMemberships.stream()
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
        return filteredMemberships.stream()
                .map(pm -> {
                    List<ProjectMember> allProjectMembers = membersByProject
                            .getOrDefault(pm.getProject().getProjectId(), List.of());
                    List<ProjectMember> otherMembers = allProjectMembers
                            .stream()
                            .filter(m -> !m.getUser().getId().equals(userId))
                            .toList();
                    Set<Long> reviewedUserIds = reviewedUserIdsByProject.getOrDefault(pm.getProject().getProjectId(), Set.of());
                    return MyProjectResponse.from(pm, otherMembers, reviewedUserIds, allProjectMembers.size());
                })
                .toList();
    }

    // 사용자 프로젝트 리스트 조회
    public List<PublicProjectResponse> getUserProjects(String userLink, SortType sort) {
        // 1. 조회 대상 User 조회
        User user = getUserByLink(userLink);

        Sort sortOption = sort == SortType.LATEST
                ? Sort.by(Sort.Direction.DESC, "project.createdAt")
                : Sort.by(Sort.Direction.ASC, "project.createdAt");

        // 2. 해당 유저가 속한 ProjectMember 목록 조회 (fetch join으로 project 포함)
        List<ProjectMember> memberships = projectMemberRepository.findAllByUser(user, sortOption);

        // 3. 각 Project의 전체 멤버 조회 후 PublicProjectResponse로 변환
        return memberships.stream()
                .filter(pm -> pm.getProject().getProjectStatus() == ProjectStatus.ARCHIVED)// 리뷰 작성 완료된 프로젝트만 필터링
                .map(PublicProjectResponse::from)
                .toList();
    }

    // 프로젝트 상세 조회
    public ProjectResponse getProject(Long projectId, Principal principal) {
        User loginUser = getLoginUser(principal);
        Project project = getProjectById(projectId);

        ProjectMember myMember =
                projectMemberRepository.findByProjectAndUser(
                        project,
                        loginUser
                ).orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage()
                ));

        List<ProjectMember> members = projectMemberRepository.findAllByProject(project);

        Set<Long> reviewedRevieweeIds = reviewRepository.findRevieweeIdsByReviewerIdAndProjectId(loginUser.getId(), projectId);

        return ProjectResponse.from(project, members, myMember, reviewedRevieweeIds);
    }

    // 프로필 페이지용 프로젝트 상세 조회
    public PublicProjectResponse getUserDetailProject(String userLink, Long projectId) {

        User user = getUserByLink(userLink);
        Project project = getProjectById(projectId);

        ProjectMember projectMember = projectMemberRepository
                .findByProjectAndUser(project, user)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage()
                ));

        return PublicProjectResponse.from(projectMember);
    }

    // 프로젝트 상태 변경
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, String newImageUrl, Principal principal) {
        Project project = getProjectById(projectId);

        // 기간 검증
        LocalDateTime startDate = request.getStartDate() != null
                ? request.getStartDate()
                : project.getStartDate();

        LocalDateTime endDate = request.getEndDate() != null
                ? request.getEndDate()
                : project.getEndDate();

        validateProjectPeriod(startDate, endDate);

        // 팀 확정 이후 수정 불가능
        if (project.getProjectStatus() == ProjectStatus.ARCHIVED) {
            throw new BusinessException(ErrorCode.INVALID_PROJECT_STATUS_EXCEPTION,
                    "팀 확정 이후에는 팀 정보 수정이 불가합니다.");
        }

        project.update(
                request.getProjectName(),
                request.getHost(),
                newImageUrl,
                request.getStartDate(),
                request.getEndDate(),
                request.getProjectUrl(),
                request.getProjectUrlType());

        User loginUser = getLoginUser(principal);

        ProjectMember myMember =
                projectMemberRepository.findByProjectAndUser(
                        project,
                        loginUser
                ).orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        List<ProjectMember> members = projectMemberRepository.findAllByProject(project);
        return ProjectResponse.from(project, members, myMember);
    }

    // 프로젝트 이미지 조회
    public String getProjectImageUrl(Long projectId) {
        return getProjectById(projectId).getProjectImage();
    }

    // Delete: 추가적으로 정책 논의 필요. -> soft deleted로 '삭제' 상태로 변경
    @Transactional
    public void deleteProject(Long projectId) {
        Project project = getProjectById(projectId);
        // 프로젝트 상태 변경
        project.deleteProject();
        projectMemberRepository.deleteAllByProject(project);
    }

    // 공통 메소드
    private User getLoginUser(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));
    }

    private void validateProjectPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "프로젝트 시작일은 종료일보다 이후일 수 없습니다."
            );
        }
    }

    private User getUserByLink(String userLink) {
        return userRepository.findByUserLinkAndIsDeletedFalse(userLink)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userLink));
    }
}
