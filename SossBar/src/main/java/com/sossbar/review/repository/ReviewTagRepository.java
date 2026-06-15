package com.sossbar.review.repository;

import com.sossbar.projects.entity.Project;
import com.sossbar.review.entity.ReviewTag;
import com.sossbar.review_profile.dto.response.TagInfoResDto;
import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

    // 받은 태그 전체 통계 조회
    @Query("""
                SELECT new com.sossbar.review_profile.dto.response.TagInfoResDto(
                    t.tagId,
                    t.tagName,
                    COUNT(rt)
                )
                FROM ReviewTag rt
                JOIN rt.tag t
                WHERE rt.review.reviewee = :user
                GROUP BY t.tagId, t.tagName
                ORDER BY COUNT(rt) DESC, t.tagId ASC 
            """)
    List<TagInfoResDto> findTagStatisticsByUser(
            @Param("user") User user
    );

    // 프로젝트별 태그 통계 조회
    @Query("""
                SELECT new com.sossbar.review_profile.dto.response.TagInfoResDto(
                    t.tagId,
                    t.tagName,
                    COUNT(rt)
                )
                FROM ReviewTag rt
                JOIN rt.tag t
                WHERE rt.review.reviewee = :user
                  AND rt.review.project = :project
                GROUP BY t.tagId, t.tagName
                ORDER BY COUNT(rt) DESC, t.tagId ASC 
            """)
    List<TagInfoResDto> findTagStatisticsByUserAndProject(
            @Param("user") User user,
            @Param("project") Project project
    );
}
