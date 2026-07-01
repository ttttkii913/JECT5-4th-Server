package com.sossbar.review_profile.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record TagListResDto(
        // top3 태그
        List<TagInfoResDto> top3Tags,

        // 전체 태그
        List<TagInfoResDto> allTags,
        LocalDateTime modifiedAt
) {
    public static TagListResDto from(List<TagInfoResDto> allTags, LocalDateTime modifiedAt) {
        List<TagInfoResDto> top3Tags =
                allTags.stream()
                        .limit(3)
                        .toList();

        return new TagListResDto(top3Tags, allTags, modifiedAt);
    }
}
