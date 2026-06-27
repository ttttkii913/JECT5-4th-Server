package com.sossbar.review.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class CommonReviewResDto {
    protected String reviewerNickname;
    protected Long reviewId;
    protected String projectImage;
    protected LocalDateTime createdAt;
}
