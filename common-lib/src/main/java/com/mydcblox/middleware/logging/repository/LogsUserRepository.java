package com.mydcblox.middleware.logging.repository;

import com.mydcblox.middleware.logging.model.LogsUser;
import com.mydcblox.middleware.logging.model.LogsUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LogsUserRepository extends JpaRepository<LogsUser, LogsUserId> {

    // Usage Note: LocalDateTime dateLimit = LocalDateTime.now().minusYears(1); limit all records to the last year
    // logsUserRepository.findAllByUserIdAndCreatedDtAfterOneYearAgo(userId, oneYearAgo);

    // Custom query to find all logs for a specific user_id ordered by created_dt desc
    @Query("SELECT l FROM LogsUser l WHERE l.id.userId = :userId AND l.id.createdDt >= :dateLimit ORDER BY l.id.createdDt DESC")
    List<LogsUser> findAllByUserIdOrderByCreatedDtDesc(Integer userId, LocalDateTime dateLimit);

    // Custom query to find all logs for a specific user_id ordered by created_dt desc
    @Query("SELECT l FROM LogsUser l WHERE l.id.userId = :userId AND l.id.createdDt >= :dateLimit ORDER BY l.id.createdDt ASC")
    List<LogsUser> findAllByUserIdOrderByCreatedDtAsc(Integer userId, LocalDateTime dateLimit);

    // Custom query to find all logs for a specific user_id ordered by created_dt desc
    @Query("SELECT l FROM LogsUser l WHERE l.organizationId = :organizationId AND l.id.createdDt >= :dateLimit ORDER BY l.id.createdDt DESC")
    List<LogsUser> findAllByOrganizationIdOrderByCreatedDtDesc(Integer organizationId, LocalDateTime dateLimit);

    // Custom query to find all logs for a specific user_id ordered by created_dt desc
    @Query("SELECT l FROM LogsUser l WHERE l.organizationId = :organizationId AND l.id.createdDt >= :dateLimit ORDER BY l.id.createdDt ASC")
    List<LogsUser> findAllByOrganizationIdOrderByCreatedDtAsc(Integer organizationId, LocalDateTime dateLimit);

}
