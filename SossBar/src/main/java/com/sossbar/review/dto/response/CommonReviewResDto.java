package com.sossbar.review.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class CommonReviewResDto {
    private final String reviewerNickname = "익명의 동료";
    protected Long reviewId;
    protected String projectImage;
    protected LocalDateTime createdAt;
}
