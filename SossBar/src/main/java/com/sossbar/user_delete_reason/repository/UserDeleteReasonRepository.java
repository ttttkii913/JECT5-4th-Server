package com.sossbar.user_delete_reason.repository;

import com.sossbar.user_delete_reason.entity.UserDeleteReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeleteReasonRepository extends JpaRepository<UserDeleteReason, Long> {

}
