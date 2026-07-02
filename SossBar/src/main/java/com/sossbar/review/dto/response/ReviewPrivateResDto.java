package com.sossbar.review.dto.response;

import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 내 프로필 전체 후기 조회
@Getter
@Builder
public class ReviewPrivateResDto extends CommonReviewResDto {
    private String projectName;
    private String host;
    private String feedback;

    private List<UserPosition> projectPositions;

    public static ReviewPrivateResDto from(Review review, ProjectMember projectMember) {
        ReviewPrivateResDto dto = ReviewPrivateResDto.builder()
                .projectName(review.getProject().getProjectName())
                .host(review.getProject().getHost())
                .projectPositions(projectMember != null ? projectMember.getProjectPositions() : null)
                .feedback(review.getFeedback())
                .build();
        dto.reviewId = review.getReviewId();
        dto.projectImage = review.getProject().getProjectImage();
        dto.createdAt = review.getCreatedAt();
        dto.reviewerNickname = review.getReviewer() != null ? review.getReviewer().getUsername() : null;
        return dto;
    }
}
