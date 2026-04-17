package com.sossbar.review.repository;

import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 중복 리뷰 검증
    boolean existsByReviewerAndReviewee(User reviewer, User reviewee);
}
