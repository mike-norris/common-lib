package com.openrangelabs.middleware.user.repository;

import com.openrangelabs.middleware.user.model.PortalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

    // Find by user ID
    Optional<PortalUser> findByUserId(Integer userId);

    // Find by username
    Optional<PortalUser> findByUsername(String username);

    // Find by email
    Optional<PortalUser> findByEmail(String email);

    // Find by organization ID
    List<PortalUser> findByOrganizationId(Integer organizationId);

    // Find by status
    List<PortalUser> findByStatus(String status);

    // Find by operation type
    List<PortalUser> findByOperationType(String operationType);

    // Find by organization and status
    List<PortalUser> findByOrganizationIdAndStatus(Integer organizationId, String status);

    // Find recent portal user events
    @Query("SELECT p FROM PortalUser p WHERE p.createdDt >= :startDate ORDER BY p.createdDt DESC")
    List<PortalUser> findRecentEvents(@Param("startDate") LocalDateTime startDate);

    // Find events by date range
    @Query("SELECT p FROM PortalUser p WHERE p.createdDt >= :startDate AND p.createdDt <= :endDate ORDER BY p.createdDt DESC")
    List<PortalUser> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // Find by organization and date range with pagination
    @Query("SELECT p FROM PortalUser p WHERE p.organizationId = :organizationId " +
            "AND p.createdDt >= :startDate AND p.createdDt <= :endDate ORDER BY p.createdDt DESC")
    Page<PortalUser> findByOrganizationAndDateRange(@Param("organizationId") Integer organizationId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    Pageable pageable);

    // Search by multiple criteria
    @Query("SELECT p FROM PortalUser p WHERE " +
            "(:organizationId IS NULL OR p.organizationId = :organizationId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:operationType IS NULL OR p.operationType = :operationType) AND " +
            "(:username IS NULL OR LOWER(p.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:email IS NULL OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "p.createdDt >= :startDate AND p.createdDt <= :endDate " +
            "ORDER BY p.createdDt DESC")
    Page<PortalUser> searchPortalUsers(@Param("organizationId") Integer organizationId,
                                       @Param("status") String status,
                                       @Param("operationType") String operationType,
                                       @Param("username") String username,
                                       @Param("email") String email,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       Pageable pageable);

    // Count by status for an organization
    @Query("SELECT p.status, COUNT(p) FROM PortalUser p WHERE p.organizationId = :organizationId " +
            "AND p.createdDt >= :startDate GROUP BY p.status")
    List<Object[]> countByStatusAndOrganization(@Param("organizationId") Integer organizationId,
                                                @Param("startDate") LocalDateTime startDate);

    // Count by operation type for an organization
    @Query("SELECT p.operationType, COUNT(p) FROM PortalUser p WHERE p.organizationId = :organizationId " +
            "AND p.createdDt >= :startDate GROUP BY p.operationType")
    List<Object[]> countByOperationTypeAndOrganization(@Param("organizationId") Integer organizationId,
                                                       @Param("startDate") LocalDateTime startDate);

    // Delete old portal user events (for cleanup)
    @Query("DELETE FROM PortalUser p WHERE p.createdDt < :cutoffDate")
    void deleteEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Check if username exists in organization
    boolean existsByUsernameAndOrganizationId(String username, Integer organizationId);

    // Check if email exists in organization
    boolean existsByEmailAndOrganizationId(String email, Integer organizationId);

    // Find most recent event for a user
    @Query("SELECT p FROM PortalUser p WHERE p.userId = :userId ORDER BY p.createdDt DESC")
    List<PortalUser> findMostRecentByUserId(@Param("userId") Integer userId, Pageable pageable);

    default Optional<PortalUser> findMostRecentByUserId(Integer userId) {
        List<PortalUser> results = findMostRecentByUserId(userId, PageRequest.of(0, 1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}