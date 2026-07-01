package com.sossbar.review.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.review.entity.Review;
import com.sossbar.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Query("SELECT r.reviewee.id FROM Review r WHERE r.reviewer.id = :reviewerId AND r.project.id = :projectId")
    Set<Long> findRevieweeIdsByReviewerIdAndProjectId(@Param("reviewerId") Long reviewerId, @Param("projectId") Long projectId);

    // 프로젝트별 후기 조회
    @Query("""
    SELECT r
    FROM Review r
    JOIN FETCH r.project
    WHERE r.reviewee.id = :userId
      AND r.project.projectId = :projectId
      AND r.project.projectStatus = com.sossbar.projects.enums.ProjectStatus.ARCHIVED
    """)
    List<Review> findAllByRevieweeIdAndProjectProjectId(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId,
            Sort sort);

    // 페이지네이션
    @Query("""
        SELECT r
        FROM Review r
        JOIN FETCH r.project
        WHERE r.reviewee.id = :userId
            AND r.project.projectStatus = com.sossbar.projects.enums.ProjectStatus.ARCHIVED
            AND (:cursor IS NULL OR r.reviewId < :cursor)        ORDER BY r.reviewId DESC
        """)
    List<Review> findByRevieweeIdWithCursor(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable);

    @Query("""
select count(r)
from Review r
where r.project = :project
  and r.reviewer.id in (
      select pm.user.id
      from ProjectMember pm
      where pm.project = :project
        and pm.isBanned = false
  )
  and r.reviewee.id in (
      select pm.user.id
      from ProjectMember pm
      where pm.project = :project
        and pm.isBanned = false
  )
""")
    long countActiveMemberReviews(@Param("project") Project project);

    @Query("""
    SELECT r
    FROM Review r
    JOIN FETCH r.project
    WHERE r.reviewee.id = :userId
      AND r.project.projectStatus = com.sossbar.projects.enums.ProjectStatus.ARCHIVED
      AND (:cursor IS NULL OR r.reviewId < :cursor)
    ORDER BY r.reviewId DESC
    """)
    List<Review> findByRevieweeIdWithCursorDesc(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable);

    @Query("""
    SELECT r
    FROM Review r
    JOIN FETCH r.project
    WHERE r.reviewee.id = :userId
      AND r.project.projectStatus = com.sossbar.projects.enums.ProjectStatus.ARCHIVED
      AND (:cursor IS NULL OR r.reviewId > :cursor)
    ORDER BY r.reviewId ASC
    """)
    List<Review> findByRevieweeIdWithCursorAsc(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable);
}
