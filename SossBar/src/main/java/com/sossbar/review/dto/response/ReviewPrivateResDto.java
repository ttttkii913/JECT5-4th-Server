package com.sossbar.review.dto.response;

import com.sossbar.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

// 내 프로필 전체 후기 조회
@Getter
@Builder
public class ReviewPrivateResDto extends CommonReviewResDto {
    private String projectName;
    private String host;
    private String positiveFeedback;
    private String negativeFeedback;

    public static ReviewPrivateResDto from(Review review) {
        ReviewPrivateResDto dto = ReviewPrivateResDto.builder()
                .projectName(review.getProject().getProjectName())
                .host(review.getProject().getHost())
                .positiveFeedback(review.getPositiveFeedback())
                .negativeFeedback(review.getNegativeFeedback())
                .build();
        dto.reviewId = review.getReviewId();
        dto.projectImage = review.getProject().getProjectImage();
        dto.createdAt = review.getCreatedAt();
        return dto;
    }
}
