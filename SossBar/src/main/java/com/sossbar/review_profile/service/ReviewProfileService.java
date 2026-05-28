package com.sossbar.review_profile.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.entity.Project;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewProfileService {

    private final ReviewTagRepository reviewTagRepository;
    private final ReviewSpectrumRepository reviewSpectrumRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    // 1. 태그 전체 조회
    public TagListResDto getAllTags(Long userId) {
        User user = getUserById(userId);
        List<TagInfoResDto> allTags = reviewTagRepository.findTagStatisticsByUser(user);

        return TagListResDto.from(allTags);
    }

    // 2. 태그 프로젝트별 조회
    public TagListResDto getTagsByProject(Long userId, Long projectId) {
        User user = getUserById(userId);
        Project project = getProjectById(projectId);

        List<TagInfoResDto> allTags = reviewTagRepository.findTagStatisticsByUserAndProject(user, project);

        return TagListResDto.from(allTags);
    }

    // 3. 스펙트럼 전체 조회
    public SpectrumListResDto getAllSpectrums(Long userId) {
        User user = getUserById(userId);

        Long totalCount = reviewSpectrumRepository.countSpectrumParticipantsByUser(user);
        List<SpectrumInfoResDto> spectrums = reviewSpectrumRepository.findSpectrumStatisticsByUser(user);

        return SpectrumListResDto.from(totalCount, spectrums);
    }

    // 4. 스펙트럼 프로젝트별 조회
    public SpectrumListResDto getSpectrumsByProject(Long userId, Long projectId) {
        User user = getUserById(userId);
        Project project = getProjectById(projectId);

        Long totalCount = reviewSpectrumRepository.countSpectrumParticipantsByUserAndProject(user, project);
        List<SpectrumInfoResDto> spectrums = reviewSpectrumRepository.findSpectrumStatisticsByUserAndProject(user, project);

        return SpectrumListResDto.from(totalCount, spectrums);
    }

    // entity 찾는 공통 메소드
    private User getUserById(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findActiveProjectById(projectId).orElseThrow(
                () -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));
    }
}
