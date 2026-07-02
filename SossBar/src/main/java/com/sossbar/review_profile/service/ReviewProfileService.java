package com.sossbar.review_profile.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.repository.ReviewSpectrumRepository;
import com.sossbar.review.repository.ReviewTagRepository;
import com.sossbar.review_profile.dto.response.SpectrumInfoResDto;
import com.sossbar.review_profile.dto.response.SpectrumListResDto;
import com.sossbar.review_profile.dto.response.TagInfoResDto;
import com.sossbar.review_profile.dto.response.TagListResDto;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewProfileService {

    private final ReviewTagRepository reviewTagRepository;
    private final ReviewSpectrumRepository reviewSpectrumRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    // 1. 태그 전체 조회
    public TagListResDto getAllTags(String userLink) {
        User user = getUserById(userLink);
        List<TagInfoResDto> allTags = reviewTagRepository.findTagStatisticsByUser(user);

        LocalDateTime modifiedAt = projectMemberRepository.findLastArchivedProjectModifiedAtByUser(user);

        return TagListResDto.from(allTags, modifiedAt);
    }

    // 2. 태그 프로젝트별 조회
    public TagListResDto getTagsByProject(String userLink, Long projectId) {
        User user = getUserById(userLink);
        Project project = getProjectById(projectId);

        List<TagInfoResDto> allTags = reviewTagRepository.findTagStatisticsByUserAndProject(user, project);

        LocalDateTime modifiedAt = project.getModifiedAt();

        return TagListResDto.from(allTags, modifiedAt);
    }

    // 3. 스펙트럼 전체 조회
    public SpectrumListResDto getAllSpectrums(String userLink) {
        User user = getUserById(userLink);

        Long totalCount = reviewSpectrumRepository.countSpectrumParticipantsByUser(user);
        List<SpectrumInfoResDto> spectrums = reviewSpectrumRepository.findSpectrumStatisticsByUser(user);
        LocalDateTime modifiedAt = projectMemberRepository.findLastArchivedProjectModifiedAtByUser(user);

        return SpectrumListResDto.from(totalCount, spectrums, modifiedAt);
    }

    // 4. 스펙트럼 프로젝트별 조회
    public SpectrumListResDto getSpectrumsByProject(String userLink, Long projectId) {
        User user = getUserById(userLink);
        Project project = getProjectById(projectId);

        Long totalCount = reviewSpectrumRepository.countSpectrumParticipantsByUserAndProject(user, project);
        List<SpectrumInfoResDto> spectrums = reviewSpectrumRepository.findSpectrumStatisticsByUserAndProject(user, project);

        LocalDateTime modifiedAt = project.getModifiedAt();

        return SpectrumListResDto.from(totalCount, spectrums, modifiedAt);
    }

    // entity 찾는 공통 메소드
    private User getUserById(String userLink) {
        return userRepository.findByUserLinkAndIsDeletedFalse(userLink).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userLink));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findActiveProjectById(projectId).orElseThrow(
                () -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));
    }
}
