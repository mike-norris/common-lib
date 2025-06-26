package com.openrangelabs.middleware.logging.repository;

import com.openrangelabs.middleware.logging.model.LogsSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogsSystemRepository extends JpaRepository<LogsSystem, Long> {

    // Find logs by service name within date range
    @Query("SELECT l FROM LogsSystem l WHERE l.serviceName = :serviceName " +
            "AND l.timestamp >= :startDate AND l.timestamp <= :endDate " +
            "ORDER BY l.timestamp DESC")
    List<LogsSystem> findByServiceNameAndDateRange(
            @Param("serviceName") String serviceName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find logs by log level
    @Query("SELECT l FROM LogsSystem l WHERE l.logLevel = :logLevel " +
            "AND l.timestamp >= :startDate ORDER BY l.timestamp DESC")
    Page<LogsSystem> findByLogLevel(
            @Param("logLevel") String logLevel,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    // Find logs with errors (includes stack traces)
    @Query("SELECT l FROM LogsSystem l WHERE l.stackTrace IS NOT NULL " +
            "AND l.timestamp >= :startDate ORDER BY l.timestamp DESC")
    Page<LogsSystem> findLogsWithErrors(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    // Find logs by correlation ID
    List<LogsSystem> findByCorrelationIdOrderByTimestampAsc(String correlationId);

    // Find logs by user ID within date range
    @Query("SELECT l FROM LogsSystem l WHERE l.userId = :userId " +
            "AND l.timestamp >= :startDate AND l.timestamp <= :endDate " +
            "ORDER BY l.timestamp DESC")
    List<LogsSystem> findByUserIdAndDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find logs by organization ID
    @Query("SELECT l FROM LogsSystem l WHERE l.organizationId = :organizationId " +
            "AND l.timestamp >= :startDate ORDER BY l.timestamp DESC")
    Page<LogsSystem> findByOrganizationId(
            @Param("organizationId") Integer organizationId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    // Find slow requests (execution time greater than threshold)
    @Query("SELECT l FROM LogsSystem l WHERE l.executionTimeMs > :threshold " +
            "AND l.timestamp >= :startDate ORDER BY l.executionTimeMs DESC")
    List<LogsSystem> findSlowRequests(
            @Param("threshold") Long threshold,
            @Param("startDate") LocalDateTime startDate);

    // Find logs by HTTP status code range (e.g., 4xx, 5xx errors)
    @Query("SELECT l FROM LogsSystem l WHERE l.responseStatus >= :minStatus " +
            "AND l.responseStatus < :maxStatus AND l.timestamp >= :startDate " +
            "ORDER BY l.timestamp DESC")
    Page<LogsSystem> findByResponseStatusRange(
            @Param("minStatus") Integer minStatus,
            @Param("maxStatus") Integer maxStatus,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    // Count logs by log level for a specific time period
    @Query("SELECT l.logLevel, COUNT(l) FROM LogsSystem l " +
            "WHERE l.timestamp >= :startDate AND l.timestamp <= :endDate " +
            "GROUP BY l.logLevel")
    List<Object[]> countByLogLevelAndDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find logs by multiple criteria (complex search)
    @Query("SELECT l FROM LogsSystem l WHERE " +
            "(:serviceName IS NULL OR l.serviceName = :serviceName) AND " +
            "(:logLevel IS NULL OR l.logLevel = :logLevel) AND " +
            "(:userId IS NULL OR l.userId = :userId) AND " +
            "(:organizationId IS NULL OR l.organizationId = :organizationId) AND " +
            "(:searchTerm IS NULL OR LOWER(l.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "l.timestamp >= :startDate AND l.timestamp <= :endDate " +
            "ORDER BY l.timestamp DESC")
    Page<LogsSystem> searchLogs(
            @Param("serviceName") String serviceName,
            @Param("logLevel") String logLevel,
            @Param("userId") Integer userId,
            @Param("organizationId") Integer organizationId,
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Delete old logs (for cleanup/retention)
    @Query("DELETE FROM LogsSystem l WHERE l.timestamp < :cutoffDate")
    void deleteLogsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find recent logs by service and environment
    @Query("SELECT l FROM LogsSystem l WHERE l.serviceName = :serviceName " +
            "AND l.environment = :environment AND l.timestamp >= :startDate " +
            "ORDER BY l.timestamp DESC")
    List<LogsSystem> findByServiceAndEnvironment(
            @Param("serviceName") String serviceName,
            @Param("environment") String environment,
            @Param("startDate") LocalDateTime startDate);
}