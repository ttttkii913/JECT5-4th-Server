package com.sossbar.review.service;

import com.sossbar.projects.entity.Project;
import com.sossbar.projects.repository.ProjectRepository;
import com.sossbar.review.dto.request.ReviewCreateReqDto;
import com.sossbar.review.dto.request.ReviewReqDto;
import com.sossbar.review.entity.Review;
import com.sossbar.review.entity.ReviewSpectrum;
import com.sossbar.review.entity.ReviewTag;
import com.sossbar.review.repository.ReviewRepository;
import com.sossbar.review.repository.ReviewSpectrumRepository;
import com.sossbar.review.repository.ReviewTagRepository;
import com.sossbar.spectrumaxis.entity.SpectrumAxis;
import com.sossbar.spectrumaxis.repository.SpectrumAxisRepository;
import com.sossbar.tag.repository.TagRepository;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
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
    public void createReview(Principal principal, ReviewCreateReqDto reviewCreateReqDto) {
        ReviewReqDto reviewReqDto = reviewCreateReqDto.getReviewReqDto();

        Long reviewerIdentifier = Long.parseLong(principal.getName());

        User reviewer = userRepository.findById(reviewerIdentifier)
                .orElseThrow(() -> new RuntimeException("해당 유저는 존재하지 않습니다: " + reviewerIdentifier));
        User reviewee = userRepository.findById(reviewReqDto.getRevieweeId())
                .orElseThrow(() -> new RuntimeException("해당 유저는 존재하지 않습니다: " + reviewReqDto.getRevieweeId()));

        if (reviewRepository.existsByReviewerAndReviewee(reviewer, reviewee)) {
            throw new IllegalStateException("이미 해당 유저에게 후기를 남긴 적이 있습니다.");
        }

        if (reviewer.getId().equals(reviewee.getId())) {
            throw new IllegalStateException("자기 자신에게 후기를 남길 수 없습니다.");
        }

        Project project = projectRepository.findById(reviewReqDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("해당 프로젝트는 존재하지 않습니다: " + reviewReqDto.getProjectId()));

        Review savedReview = reviewRepository.save(reviewReqDto.toEntity(reviewer, reviewee, project));

        for (Long tagId : reviewReqDto.getTagIds()) {
            reviewTagRepository.save(ReviewTag.builder()
                    .review(savedReview)
                    .tag(tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("해당 태그는 존재하지 않습니다: " + tagId)))
                    .build());
        }

        // 각 스펙트럼 축 당 저장하기
        List<ReviewSpectrum> reviewSpectrums = reviewCreateReqDto.getSpectrumReqDtos().stream()
                .map(spectrumReqDto -> {
                    SpectrumAxis spectrumAxis = spectrumAxisRepository.findById(spectrumReqDto.getSpectrumAxisId()).orElseThrow(() -> new RuntimeException("해당 스펙트럼은 존재하지 않습니다: " + spectrumReqDto.getSpectrumAxisId()));

                    return ReviewSpectrum.builder()
                            .review(savedReview)
                            .spectrumAxis(spectrumAxis)
                            .strength(spectrumReqDto.getSpectrumStrength())
                            .build();
                        })
                .collect(Collectors.toList());

        reviewSpectrumRepository.saveAll(reviewSpectrums);
    }
}
