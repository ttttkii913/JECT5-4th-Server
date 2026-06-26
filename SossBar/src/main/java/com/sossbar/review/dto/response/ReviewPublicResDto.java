package com.sossbar.review.dto.response;

import com.sossbar.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

// 다른 사용자 전체 후기 조회 DTO
@Getter
@Builder
public class ReviewPublicResDto extends CommonReviewResDto {
    private String projectName;
    private String host;
    private String positiveFeedback;

    public static ReviewPublicResDto from(Review review) {
        ReviewPublicResDto dto = ReviewPublicResDto.builder()
                .projectName(review.getProject().getProjectName())
                .host(review.getProject().getHost())
                .positiveFeedback(review.getPositiveFeedback())
                .build();
        dto.reviewId = review.getReviewId();
        dto.projectImage = review.getProject().getProjectImage();
        dto.createdAt = review.getCreatedAt();
        dto.reviewerNickname = review.getReviewer() != null ? review.getReviewer().getUsername() : null;
        return dto;
    }
}
