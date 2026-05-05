package com.sossbar.review.service;

import com.sossbar.review.dto.response.FormDataResDto;
import com.sossbar.spectrumaxis.dto.response.SpectrumAxisResDto;
import com.sossbar.spectrumaxis.repository.SpectrumAxisRepository;
import com.sossbar.tag.dto.response.TagResDto;
import com.sossbar.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FormDataService {

    private final TagRepository tagRepository;
    private final SpectrumAxisRepository spectrumAxisRepository;

    public FormDataResDto getFormData() {
        List<TagResDto> tags = tagRepository.findAll().stream()
                .map(tag -> new TagResDto(tag.getTagId(),
                        tag.getTagName()))
                .toList();

        List<SpectrumAxisResDto> spectrumAxes = spectrumAxisRepository.findAll().stream()
                .map(spectrumAxis -> new SpectrumAxisResDto(
                        spectrumAxis.getSpectrumAxisId(),
                        spectrumAxis.getAxisName(),
                        spectrumAxis.getLeftLabel(),
                        spectrumAxis.getRightLabel()
                ))
                .toList();

        return new FormDataResDto(tags, spectrumAxes);
    }
}
