package com.sossbar.review.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.dto.request.ReviewCreateReqDto;
import com.sossbar.review.dto.request.ReviewReqDto;
import com.sossbar.review.dto.request.SpectrumReqDto;
import com.sossbar.review.dto.response.*;
import com.sossbar.review.entity.Review;
import com.sossbar.review.entity.ReviewSpectrum;
import com.sossbar.review.entity.ReviewTag;
import com.sossbar.review.entity.ReviewValidReason;
import com.sossbar.review.repository.ReviewRepository;
import com.sossbar.review.repository.ReviewSpectrumRepository;
import com.sossbar.review.repository.ReviewTagRepository;
import com.sossbar.spectrumaxis.entity.SpectrumAxis;
import com.sossbar.spectrumaxis.repository.SpectrumAxisRepository;
import com.sossbar.tag.entity.Tag;
import com.sossbar.tag.repository.TagRepository;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TagRepository tagRepository;
    private final SpectrumAxisRepository spectrumAxisRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final ReviewSpectrumRepository reviewSpectrumRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ReviewCreateResDto createReview(Principal principal, ReviewCreateReqDto reviewCreateReqDto) {
        ReviewReqDto reviewReqDto = reviewCreateReqDto.getReviewReqDto();
        List<ReviewTag> reviewTags = new ArrayList<>();

        long reviewerIdentifier;

        try {
            reviewerIdentifier = Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }

        User reviewer =  getUser(reviewerIdentifier);
        User reviewee = getUser(reviewReqDto.getRevieweeId());
        Project project = getProject(reviewReqDto.getProjectId());

        ReviewValidReason reason =
                validateReviewCondition(reviewer, reviewee, project);

        if (reason != ReviewValidReason.VALID) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    reason.name()
            );
        }

        Review savedReview = reviewRepository.save(reviewReqDto.toEntity(reviewer, reviewee, project));

        // 태그 목록 저장
        List<Long> tagIds = reviewReqDto.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);

            if(tags.size() != tagIds.size()) {
                throw new BusinessException(ErrorCode.TAG_NOT_FOUND, "");
            }

            reviewTags = tags.stream()
                    .map(tag -> ReviewTag.builder()
                            .review(savedReview)
                            .tag(tag)
                            .build())
                    .collect(Collectors.toList());

            reviewTagRepository.saveAll(reviewTags);
        }

        // 각 스펙트럼 축 당 저장하기
        List<Long> spectrumAxisIds = reviewCreateReqDto.getSpectrumReqDtos().stream()
                .map(SpectrumReqDto::getSpectrumAxisId)
                .distinct()
                .collect(Collectors.toList());

        List<SpectrumAxis> spectrumAxes = spectrumAxisRepository.findAllById(spectrumAxisIds);

        Map<Long, SpectrumAxis> spectrumAxisMap = spectrumAxes.stream()
                        .collect(Collectors.toMap(SpectrumAxis::getSpectrumAxisId, axis -> axis));

        if(spectrumAxisMap.size() != spectrumAxisIds.size()) {
            throw new BusinessException(ErrorCode.SPECTRUM_NOT_FOUND, "");
        }

        List<ReviewSpectrum> reviewSpectrums = reviewCreateReqDto.getSpectrumReqDtos().stream()
                        .map(dto -> ReviewSpectrum.builder()
                                .review(savedReview)
                                .spectrumAxis(spectrumAxisMap.get(dto.getSpectrumAxisId()))
                                .strength(dto.getSpectrumStrength())
                                .build())
                        .collect(Collectors.toList());

        reviewSpectrumRepository.saveAll(reviewSpectrums);

        return ReviewCreateResDto.from(savedReview, reviewTags, reviewSpectrums);
    }

    // 전체 후기 조회
    public List<CommonReviewResDto> getReviews(Principal principal, Long userId) {
        Long loginUserId = (principal != null) ? Long.parseLong(principal.getName()) : null;
        List<Review> reviews = reviewRepository.findAllByRevieweeId(userId);

        // 내가 내 전체 후기 조회
        if (userId.equals(loginUserId)) {
            return reviews.stream()
                    .map(ReviewPrivateResDto::from)
                    .collect(Collectors.toList());
        }
        // 다른 사용자 전체 후기 조회
        return reviews.stream()
                .map(ReviewPublicResDto::from)
                .collect(Collectors.toList());
    }

    // 프로젝트별 후기 조회
    public List<CommonReviewResDto> getReviewsByProject(Principal principal, Long userId, Long projectId) {
        Long loginUserId = (principal != null) ? Long.parseLong(principal.getName()) : null;
        List<Review> reviews = reviewRepository.findAllByRevieweeIdAndProjectProjectId(userId, projectId);

        // 내 프로젝트별 후기 조회
        if(userId.equals(loginUserId)) {
            return reviews.stream()
                    .map(ReviewPrivateResDto::from)
                    .collect(Collectors.toList());
        }
        // 다른 사용자 프로젝트별 후기 조회
        return reviews.stream()
                .map(ReviewPublicResDto::from)
                .collect(Collectors.toList());
    }

    // 후기 작성 가능 여부 검증
    @Transactional(readOnly = true)
    public ReviewValidResDto validateReview(Principal principal, Long projectId, Long revieweeId) {
        Long reviewerId = Long.parseLong(principal.getName());

        User reviewer = getUser(reviewerId);
        User reviewee = getUser(revieweeId);
        Project project = getProject(projectId);

        ReviewValidReason reason =
                validateReviewCondition(reviewer, reviewee, project);

        return reason == ReviewValidReason.VALID
                ? ReviewValidResDto.valid()
                : ReviewValidResDto.invalid(reason);
    }

    // 공통 검증 메소드
    private ReviewValidReason validateReviewCondition(User reviewer, User reviewee, Project project) {
        if (reviewer.getId().equals(reviewee.getId())) {
            return ReviewValidReason.SELF_REVIEW;
        }

        if (!projectMemberRepository.existsByProjectAndUser(project, reviewer)) {
            return ReviewValidReason.REVIEWER_NOT_IN_PROJECT;
        }

        if (!projectMemberRepository.existsByProjectAndUser(project, reviewee)) {
            return ReviewValidReason.REVIEWEE_NOT_IN_PROJECT;
        }

        if (reviewRepository.existsByReviewerAndRevieweeAndProject(reviewer, reviewee, project)) {
            return ReviewValidReason.ALREADY_REVIEWED;
        }

        return ReviewValidReason.VALID;
    }

    // 엔티티 조회 공통 메소드
    private Project getProject(Long projectId) {
        return projectRepository.findActiveProjectById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND_EXCEPTION,
                        ErrorCode.PROJECT_NOT_FOUND_EXCEPTION.getMessage() + projectId));
    }

    private User getUser(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }
}
