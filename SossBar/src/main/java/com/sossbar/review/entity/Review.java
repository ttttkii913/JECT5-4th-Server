package com.sossbar.review.entity;

import com.sossbar.projects.entity.Project;
import com.sossbar.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column
    private String positiveFeedback;

    @Column
    private String negativeFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    private User reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTag> reviewTags = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewSpectrum> reviewSpectrums = new ArrayList<>();

    @Builder
    public Review(String positiveFeedback, String negativeFeedback, User reviewer, User reviewee, Project project) {
        this.positiveFeedback = positiveFeedback;
        this.negativeFeedback = negativeFeedback;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.project = project;
    }
}