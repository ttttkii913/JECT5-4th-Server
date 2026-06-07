package com.sossbar.review.dto.response;

import com.sossbar.review.entity.ReviewValidReason;

public record ReviewValidResDto(
        Boolean canReview,
        ReviewValidReason reviewValidReason
) {
    public static ReviewValidResDto valid() {
        return new ReviewValidResDto(true, ReviewValidReason.VALID);
    }

    public static ReviewValidResDto invalid(ReviewValidReason reason) {
        return new ReviewValidResDto(false, reason);
    }
}
