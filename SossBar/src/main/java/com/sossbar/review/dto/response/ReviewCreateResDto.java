package com.sossbar.review.dto.response;

import com.sossbar.review.entity.Review;
import com.sossbar.review.entity.ReviewSpectrum;
import com.sossbar.review.entity.ReviewTag;
import com.sossbar.tag.dto.response.TagResDto;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ReviewCreateResDto (
        ReviewResDto reviewResDto,
        List<SpectrumResDto> spectrumResDtos
) {
    public static ReviewCreateResDto from(Review savedReview, List<ReviewTag> reviewTags, List<ReviewSpectrum> reviewSpectrums) {
      List<TagResDto> tagResDtos = reviewTags.stream()
              .map(reviewTag -> new TagResDto(
                      reviewTag.getTag().getTagId(),
                      reviewTag.getTag().getTagName()
              ))
              .collect(Collectors.toList());

      ReviewResDto reviewResDto = new ReviewResDto(
              savedReview.getPositiveFeedback(),
              savedReview.getNegativeFeedback(),
              savedReview.getReviewee().getId(),
              savedReview.getProject().getProjectId(),
              tagResDtos
      );

      List<SpectrumResDto> spectrumResDtos = reviewSpectrums.stream()
              .map(reviewSpectrum -> SpectrumResDto.builder()
                      .spectrumAxisId(reviewSpectrum.getSpectrumAxis().getSpectrumAxisId())
                      .spectrumStrength(reviewSpectrum.getStrength())
                      .build())
              .collect(Collectors.toList());

      return ReviewCreateResDto.builder()
              .reviewResDto(reviewResDto)
              .spectrumResDtos(spectrumResDtos)
              .build();
    }
}
