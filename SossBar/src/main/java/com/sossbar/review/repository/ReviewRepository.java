package com.sossbar.review.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 중복 후기 검증
    boolean existsByReviewerAndRevieweeAndProject(User reviewer, User reviewee, Project project);

    // 사용자가 받은 후기 목록 (프로젝트 생성 날짜 기준 내림차순)
    @Query("""
        SELECT r 
        FROM Review r
        JOIN FETCH r.project 
        WHERE r.reviewee.id = :userId
        ORDER BY r.project.createdAt DESC
        """)
    List<Review> findAllByRevieweeId(@Param("userId") Long userId);

    @Query("SELECT r.reviewee.id FROM Review r WHERE r.reviewer.id = :reviewerId AND r.project.id = :projectId")
    Set<Long> findRevieweeIdsByReviewerIdAndProjectId(@Param("reviewerId") Long reviewerId, @Param("projectId") Long projectId);

    @Query("SELECT r FROM Review r JOIN FETCH r.project WHERE r.reviewee.id = :userId AND r.project.id = :projectId")
    List<Review> findAllByRevieweeIdAndProjectProjectId(@Param("userId")  Long userId, @Param("projectId") Long projectId);

    // 페이지네이션
    @Query("""
        SELECT r
        FROM Review r
        JOIN FETCH r.project
        WHERE r.reviewee.id = :userId
        AND (:cursor IS NULL OR r.reviewId < :cursor)
        ORDER BY r.reviewId DESC
        """)
    List<Review> findByRevieweeIdWithCursor(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable);
}
