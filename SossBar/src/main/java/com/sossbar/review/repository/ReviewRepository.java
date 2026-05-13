package com.sossbar.review.repository;

import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 중복 후기 검증
    boolean existsByReviewerAndReviewee(User reviewer, User reviewee);

    // 사용자가 받은 후기 목록 (프로젝트 생성 날짜 기준 내림차순)
    @Query("""
        SELECT r 
        FROM Review r
        JOIN FETCH r.project 
        WHERE r.reviewee.id = :userId
        ORDER BY r.project.createdAt DESC
        """)
    List<Review> findAllByRevieweeId(@Param("userId") Long userId);

}
