package com.sossbar.review.entity;

import com.sossbar.spectrumaxis.entity.SpectrumAxis;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class ReviewSpectrum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewSpectrumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spectrum_axis_id")
    private SpectrumAxis spectrumAxis;

    @Column
    private Integer strength;

    public void setReview(Review review) {
        this.review = review;
    }

    public void setSpectrumAxis(SpectrumAxis spectrumAxis) {
        this.spectrumAxis = spectrumAxis;
    }

}
