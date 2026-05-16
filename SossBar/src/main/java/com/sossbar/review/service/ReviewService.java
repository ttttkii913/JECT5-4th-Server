package com.sossbar.review.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.dto.request.ReviewCreateReqDto;
import com.sossbar.review.dto.request.ReviewReqDto;
import com.sossbar.review.dto.request.SpectrumReqDto;
import com.sossbar.review.dto.response.CommonReviewResDto;
import com.sossbar.review.dto.response.ReviewCreateResDto;
import com.sossbar.review.dto.response.ReviewPrivateResDto;
import com.sossbar.review.dto.response.ReviewPublicResDto;
import com.sossbar.review.entity.Review;
import com.sossbar.review.entity.ReviewSpectrum;
import com.sossbar.review.entity.ReviewTag;
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

        User reviewer = userRepository.findById(reviewerIdentifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION, reviewerIdentifier+""));
        User reviewee = userRepository.findById(reviewReqDto.getRevieweeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION, reviewerIdentifier+""));

        if (reviewRepository.existsByReviewerAndReviewee(reviewer, reviewee)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REVIEW_EXCEPTION, reviewee.getId()+"");
        }

        if (reviewer.getId().equals(reviewee.getId())) {
            throw new BusinessException(ErrorCode.SELF_REVIEW_NOT_ALLOWED, "");
        }

        Project project = projectRepository.findById(reviewReqDto.getProjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND_EXCEPTION, reviewReqDto.getProjectId()+""));

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

        if(spectrumAxisMap.size() != spectrumAxes.size()) {
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

    public List<CommonReviewResDto> getReviews(Principal principal, Long userId) {
        Long loginUserId = (principal != null) ? Long.parseLong(principal.getName()) : null;
        List<Review> reviews = reviewRepository.findAllByRevieweeId(userId);

        // 내가 내 전체 후기 조회
        if(userId.equals(loginUserId)) {
            return reviews.stream()
                    .map(ReviewPrivateResDto::from)
                    .collect(Collectors.toList());
        }
        // 다른 사용자 전체 후기 조회
        return reviews.stream()
                .map(ReviewPublicResDto::from)
                .collect(Collectors.toList());
        }
}
