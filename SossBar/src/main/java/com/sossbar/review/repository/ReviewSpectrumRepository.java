package com.sossbar.review.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.review.entity.ReviewSpectrum;
import com.sossbar.review_profile.dto.response.SpectrumInfoResDto;
import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSpectrumRepository extends JpaRepository<ReviewSpectrum, Long> {

    // 전체 스펙트럼 통계 조회
    @Query("""
                SELECT new com.sossbar.review_profile.dto.response.SpectrumInfoResDto(
                    sa.spectrumAxisId,
                    sa.axisName,
                    CAST(ROUND(AVG(rs.strength)) as integer),
                    SUM(CASE WHEN rs.strength <= 3 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN rs.strength >= 4 THEN 1 ELSE 0 END)
                )
                FROM ReviewSpectrum rs
                JOIN rs.spectrumAxis sa
                WHERE rs.review.reviewee = :user
                GROUP BY sa.spectrumAxisId, sa.axisName
            """)
    List<SpectrumInfoResDto> findSpectrumStatisticsByUser(
            @Param("user") User user
    );

    // 프로젝트별 스펙트럼 통계 조회
    @Query("""
                SELECT new com.sossbar.review_profile.dto.response.SpectrumInfoResDto(
                    sa.spectrumAxisId,
                    sa.axisName,
                    CAST(ROUND(AVG(rs.strength)) as integer),
                    SUM(CASE WHEN rs.strength <= 3 THEN 1 ELSE 0 END),
                    SUM(CASE WHEN rs.strength >= 4 THEN 1 ELSE 0 END)
                )
                FROM ReviewSpectrum rs
                JOIN rs.spectrumAxis sa
                WHERE rs.review.reviewee = :user
                  AND rs.review.project = :project
                GROUP BY sa.spectrumAxisId, sa.axisName
            """)
    List<SpectrumInfoResDto> findSpectrumStatisticsByUserAndProject(
            @Param("user") User user,
            @Param("project") Project project
    );

    @Query("""
                SELECT COUNT(DISTINCT rs.review.reviewer.id)
                FROM ReviewSpectrum rs
                WHERE rs.review.reviewee = :user
            """)
    Long countSpectrumParticipantsByUser(@Param("user") User user);

    @Query("""
                SELECT COUNT(DISTINCT rs.review.reviewer.id)
                FROM ReviewSpectrum rs
                WHERE rs.review.reviewee = :user
                  AND rs.review.project = :project
            """)
    Long countSpectrumParticipantsByUserAndProject(
            @Param("user") User user,
            @Param("project") Project project
    );
}
