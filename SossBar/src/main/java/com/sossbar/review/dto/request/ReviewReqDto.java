package com.sossbar.review.dto.request;

import com.sossbar.projects.entity.Project;
import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

// 리뷰 + 태그 DTO
@Getter
public class ReviewReqDto {
    @NotBlank
    @Size(min = 10, max = 1000)
    private String feedback;

    @NotNull
    @Positive
    private Long revieweeId;

    @NotNull
    @Positive
    private Long projectId;

    private List<Long> tagIds;

    public Review toEntity(User reviewer, User reviewee, Project project) {
        return Review.builder()
                .feedback(feedback)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .project(project)
                .build();
    }
}
