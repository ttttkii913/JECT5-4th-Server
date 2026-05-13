package com.sossbar.review.dto.response;

import com.sossbar.review.entity.Review;
import lombok.Builder;

// 내 프로필 전체 후기 조회
@Builder
public class ReviewPrivateResDto extends CommonReviewResDto {
    private String projectName;
    private String host;
    private String positiveFeedback;
    private String negativeFeedback;

    public static ReviewPrivateResDto from(Review review) {
        return ReviewPrivateResDto.builder()
                .projectName(review.getProject().getProjectName())
                .host(review.getProject().getHost())
                .positiveFeedback(review.getPositiveFeedback())
                .negativeFeedback(review.getNegativeFeedback())
                .build();
    }
}
