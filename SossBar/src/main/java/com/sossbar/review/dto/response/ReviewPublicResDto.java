package com.sossbar.review.dto.response;

import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 다른 사용자 전체 후기 조회 DTO
@Getter
@Builder
public class ReviewPublicResDto extends CommonReviewResDto {
    private String projectName;
    private String host;
    private String feedback;

    private List<UserPosition> projectPositions;

    public static ReviewPublicResDto from(Review review, ProjectMember projectMember) {
        ReviewPublicResDto dto = ReviewPublicResDto.builder()
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
