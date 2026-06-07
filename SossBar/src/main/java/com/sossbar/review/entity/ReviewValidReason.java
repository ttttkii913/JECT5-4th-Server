package com.sossbar.review.entity;

public enum ReviewValidReason {
    VALID,                      // 가능
    SELF_REVIEW,                // 본인에게 리뷰
    REVIEWER_NOT_IN_PROJECT,    // 리뷰이가 해당 프로젝트에 없음
    REVIEWEE_NOT_IN_PROJECT,    // 리뷰어가 해당 프로젝트에 없음
    ALREADY_REVIEWED,           // 이미 작성된 리뷰
    PROJECT_NOT_COMPLETED       // 프로젝트 확정되지 않음
}
