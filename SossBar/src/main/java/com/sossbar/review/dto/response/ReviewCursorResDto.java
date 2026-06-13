package com.sossbar.review.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewCursorResDto {
    private List<CommonReviewResDto> reviews;
    private Long nextCursor;
    private boolean hasNext;
}
