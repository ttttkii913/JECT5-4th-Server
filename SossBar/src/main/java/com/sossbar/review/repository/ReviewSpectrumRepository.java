package com.sossbar.review.repository;

import com.sossbar.review.entity.ReviewSpectrum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSpectrumRepository extends JpaRepository<ReviewSpectrum, Long> {
}
