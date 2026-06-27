package com.sossbar.review.dto.response;

import com.sossbar.tag.dto.response.TagResDto;

import java.util.List;

public record ReviewResDto (
        String feedback,
        Long revieweeId,
        Long projectId,
        List<TagResDto> tagResDtos
) {
}
