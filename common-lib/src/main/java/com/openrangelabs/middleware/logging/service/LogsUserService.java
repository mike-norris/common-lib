package com.openrangelabs.middleware.logging.service;

import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.mapper.LogsUserMapper;
import com.openrangelabs.middleware.logging.model.LogsUser;
import com.openrangelabs.middleware.logging.model.LogsUserId;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.logging.repository.LogsUserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user logs
 */
@Service
@Transactional
@Validated
public class LogsUserService {

    private static final Logger logger = LoggerFactory.getLogger(LogsUserService.class);

    private final LogsUserRepository repository;
    private final LogsUserMapper mapper;

    @Autowired
    public LogsUserService(LogsUserRepository repository, LogsUserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Save a new user log entry
     * @param dto the log entry to save
     * @return the saved log entry
     */
    public LogsUserDTO saveLog(@Valid @NotNull LogsUserDTO dto) {
        try {
            // Validate log type if provided
            if (dto.getType() != null) {
                UserLogType.fromCode(dto.getType());
            }

            LogsUser entity = mapper.toEntity(dto);
            LogsUser saved = repository.save(entity);
            logger.debug("Saved user log for user {} at {}", dto.getUserId(), dto.getCreatedDt());
            return mapper.toDTO(saved);
        } catch (Exception e) {
            logger.error("Error saving user log: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user log", e);
        }
    }

    /**
     * Save multiple log entries in batch
     * @param logs the list of log entries to save
     * @return the list of saved log entries
     */
    public List<LogsUserDTO> saveLogsBatch(@Valid List<LogsUserDTO> logs) {
        try {
            // Validate all log types
            logs.forEach(log -> {
                if (log.getType() != null) {
                    UserLogType.fromCode(log.getType());
                }
            });

            List<LogsUser> entities = mapper.toEntityList(logs);
            List<LogsUser> saved = repository.saveAll(entities);
            logger.debug("Saved batch of {} user logs", logs.size());
            return mapper.toDTOList(saved);
        } catch (Exception e) {
            logger.error("Error saving batch of user logs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save batch of user logs", e);
        }
    }

    /**
     * Find a specific log by composite key
     * @param userId the user ID
     * @param createdDt the creation timestamp
     * @return the log if found
     */
    @Transactional(readOnly = true)
    public Optional<LogsUserDTO> findById(@NotNull Integer userId, @NotNull LocalDateTime createdDt) {
        LogsUserId id = LogsUserId.builder()
                .userId(userId)
                .createdDt(createdDt)
                .build();

        return repository.findById(id)
                .map(mapper::toDTO);
    }

    /**
     * Find all logs for a specific user with date limit
     * @param userId the user ID
     * @param dateLimit the date limit (logs after this date)
     * @param ascending true for ascending order, false for descending
     * @return list of user logs
     */
    @Transactional(readOnly = true)
    public List<LogsUserDTO> findByUserId(@NotNull Integer userId,
                                          LocalDateTime dateLimit,
                                          boolean ascending) {
        if (dateLimit == null) {
            dateLimit = LocalDateTime.now().minusYears(1);
        }

        List<LogsUser> logs = ascending
                ? repository.findAllByUserIdOrderByCreatedDtAsc(userId, dateLimit)
                : repository.findAllByUserIdOrderByCreatedDtDesc(userId, dateLimit);

        return mapper.toDTOList(logs);
    }

    /**
     * Find all logs for a specific organization with date limit
     * @param organizationId the organization ID
     * @param dateLimit the date limit (logs after this date)
     * @param ascending true for ascending order, false for descending
     * @return list of user logs
     */
    @Transactional(readOnly = true)
    public List<LogsUserDTO> findByOrganizationId(@NotNull Integer organizationId,
                                                  LocalDateTime dateLimit,
                                                  boolean ascending) {
        if (dateLimit == null) {
            dateLimit = LocalDateTime.now().minusYears(1);
        }

        List<LogsUser> logs = ascending
                ? repository.findAllByOrganizationIdOrderByCreatedDtAsc(organizationId, dateLimit)
                : repository.findAllByOrganizationIdOrderByCreatedDtDesc(organizationId, dateLimit);

        return mapper.toDTOList(logs);
    }

    /**
     * Log a user action
     * @param userId the user ID
     * @param organizationId the organization ID
     * @param type the log type
     * @param description the action description
     * @return the created log entry
     */
    public LogsUserDTO logUserAction(@NotNull Integer userId,
                                     @NotNull Integer organizationId,
                                     @NotNull String type,
                                     String description) {
        LogsUserDTO log = LogsUserDTO.builder()
                .userId(userId)
                .organizationId(organizationId)
                .type(type)
                .description(description)
                .createdDt(LocalDateTime.now())
                .build();

        return saveLog(log);
    }

    /**
     * Log a user login
     * @param userId the user ID
     * @param organizationId the organization ID
     * @param description optional description (e.g., IP address, browser)
     * @return the created log entry
     */
    public LogsUserDTO logLogin(@NotNull Integer userId,
                                @NotNull Integer organizationId,
                                String description) {
        return logUserAction(userId, organizationId, UserLogType.LOGIN.getCode(), description);
    }

    /**
     * Log a user logout
     * @param userId the user ID
     * @param organizationId the organization ID
     * @param description optional description
     * @return the created log entry
     */
    public LogsUserDTO logLogout(@NotNull Integer userId,
                                 @NotNull Integer organizationId,
                                 String description) {
        return logUserAction(userId, organizationId, UserLogType.LOGOUT.getCode(), description);
    }

    /**
     * Count logs by type for a user within a date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return count of logs grouped by type
     */
    @Transactional(readOnly = true)
    public List<Object[]> countUserLogsByType(@NotNull Integer userId,
                                              @NotNull LocalDateTime startDate,
                                              @NotNull LocalDateTime endDate) {
        List<LogsUser> logs = repository.findAllByUserIdOrderByCreatedDtDesc(userId, startDate);

        // Filter by end date and group by type
        return logs.stream()
                .filter(log -> log.getId().getCreatedDt().isBefore(endDate))
                .collect(java.util.stream.Collectors.groupingBy(
                        LogsUser::getType,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Delete old logs for data retention
     * @param cutoffDate the cutoff date
     * @return number of deleted logs
     */
    @Transactional
    public long deleteOldLogs(@NotNull LocalDateTime cutoffDate) {
        logger.info("Deleting user logs older than: {}", cutoffDate);

        // Since we don't have a direct delete method, we need to find and delete
        // This is a simplified version - in production you'd want a more efficient approach
        List<LogsUser> oldLogs = repository.findAll().stream()
                .filter(log -> log.getId().getCreatedDt().isBefore(cutoffDate))
                .collect(java.util.stream.Collectors.toList());

        repository.deleteAll(oldLogs);
        logger.info("Deleted {} old user logs", oldLogs.size());
        return oldLogs.size();
    }
}