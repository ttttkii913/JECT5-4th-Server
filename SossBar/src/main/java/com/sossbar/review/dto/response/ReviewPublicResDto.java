package com.sossbar.review.dto.response;

import com.sossbar.review.entity.Review;
import lombok.Builder;

// 다른 사용자 전체 후기 조회 DTO
@Builder
public class ReviewPublicResDto extends CommonReviewResDto {
    private String projectName;
    private String host;
    private String positiveFeedback;

    public static ReviewPublicResDto from(Review review) {
        return ReviewPublicResDto.builder()
                .projectName(review.getProject().getProjectName())
                .host(review.getProject().getHost())
                .positiveFeedback(review.getPositiveFeedback())
                .build();
    }
}
