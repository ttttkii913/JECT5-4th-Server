package com.sossbar.spectrumaxis.entity;

import com.sossbar.review.entity.ReviewSpectrum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SpectrumAxis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spectrumAxisId;

    @Column(nullable = false, unique = true)
    private String axisName;

    @Column
    private String leftLabel;

    @Column
    private String rightLabel;

    @OneToMany(mappedBy = "spectrumAxis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewSpectrum> reviewSpectrums = new ArrayList<>();

}
