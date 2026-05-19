package com.sossbar.review_profile.dto.response;

public record TagInfoResDto(
        Long tagId,
        String tagName,

        // 받은 태그 개수 추가 - TagResDto와의 차이점
        Long count
) {
}
