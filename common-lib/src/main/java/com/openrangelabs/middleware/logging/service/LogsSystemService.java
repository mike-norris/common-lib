package com.openrangelabs.middleware.logging.service;

import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.mapper.LogsSystemMapper;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.LogsSystem;
import com.openrangelabs.middleware.logging.repository.LogsSystemRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing system logs
 */
@Service
@Transactional
@Validated
public class LogsSystemService {

    private static final Logger logger = LoggerFactory.getLogger(LogsSystemService.class);

    private final LogsSystemRepository repository;
    private final LogsSystemMapper mapper;

    @Autowired
    public LogsSystemService(LogsSystemRepository repository, LogsSystemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Save a new system log entry
     * @param dto the log entry to save
     * @return the saved log entry
     */
    public LogsSystemDTO saveLog(@Valid @NotNull LogsSystemDTO dto) {
        try {
            // Validate log level
            LogLevel.fromString(dto.getLogLevel());

            LogsSystem entity = mapper.toEntity(dto);
            LogsSystem saved = repository.save(entity);
            return mapper.toDTO(saved);
        } catch (Exception e) {
            logger.error("Error saving system log: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save system log", e);
        }
    }

    /**
     * Save multiple log entries in batch
     * @param logs the list of log entries to save
     * @return the list of saved log entries
     */
    public List<LogsSystemDTO> saveLogsBatch(@Valid List<LogsSystemDTO> logs) {
        try {
            // Validate all log levels
            logs.forEach(log -> LogLevel.fromString(log.getLogLevel()));

            List<LogsSystem> entities = mapper.toEntityList(logs);
            List<LogsSystem> saved = repository.saveAll(entities);
            return mapper.toDTOList(saved);
        } catch (Exception e) {
            logger.error("Error saving batch of system logs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save batch of system logs", e);
        }
    }

    /**
     * Find logs by service name within a date range
     * @param serviceName the name of the service
     * @param startDate the start date
     * @param endDate the end date
     * @return list of matching logs
     */
    @Transactional(readOnly = true)
    public List<LogsSystemDTO> findByServiceAndDateRange(String serviceName,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate) {
        List<LogsSystem> logs = repository.findByServiceNameAndDateRange(serviceName, startDate, endDate);
        return mapper.toDTOList(logs);
    }

    /**
     * Find logs by log level with pagination
     * @param logLevel the log level to filter by
     * @param startDate the start date
     * @param pageable pagination information
     * @return page of matching logs
     */
    @Transactional(readOnly = true)
    public Page<LogsSystemDTO> findByLogLevel(String logLevel,
                                              LocalDateTime startDate,
                                              Pageable pageable) {
        // Validate log level
        LogLevel.fromString(logLevel);

        Page<LogsSystem> page = repository.findByLogLevel(logLevel, startDate, pageable);
        return page.map(mapper::toDTO);
    }

    /**
     * Find all logs with errors (containing stack traces)
     * @param startDate the start date
     * @param pageable pagination information
     * @return page of error logs
     */
    @Transactional(readOnly = true)
    public Page<LogsSystemDTO> findErrorLogs(LocalDateTime startDate, Pageable pageable) {
        Page<LogsSystem> page = repository.findLogsWithErrors(startDate, pageable);
        return page.map(mapper::toDTO);
    }

    /**
     * Find logs by correlation ID
     * @param correlationId the correlation ID
     * @return list of logs with the same correlation ID
     */
    @Transactional(readOnly = true)
    public List<LogsSystemDTO> findByCorrelationId(String correlationId) {
        List<LogsSystem> logs = repository.findByCorrelationIdOrderByTimestampAsc(correlationId);
        return mapper.toDTOList(logs);
    }

    /**
     * Find slow requests above a threshold
     * @param thresholdMs execution time threshold in milliseconds
     * @param startDate the start date
     * @return list of slow request logs
     */
    @Transactional(readOnly = true)
    public List<LogsSystemDTO> findSlowRequests(Long thresholdMs, LocalDateTime startDate) {
        List<LogsSystem> logs = repository.findSlowRequests(thresholdMs, startDate);
        return mapper.toDTOList(logs);
    }

    /**
     * Search logs with multiple criteria
     * @param serviceName optional service name filter
     * @param logLevel optional log level filter
     * @param userId optional user ID filter
     * @param organizationId optional organization ID filter
     * @param searchTerm optional search term for message content
     * @param startDate start date for the search
     * @param endDate end date for the search
     * @param pageable pagination information
     * @return page of matching logs
     */
    @Transactional(readOnly = true)
    public Page<LogsSystemDTO> searchLogs(String serviceName,
                                          String logLevel,
                                          Integer userId,
                                          Integer organizationId,
                                          String searchTerm,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          Pageable pageable) {
        // Validate log level if provided
        if (logLevel != null) {
            LogLevel.fromString(logLevel);
        }

        Page<LogsSystem> page = repository.searchLogs(
                serviceName, logLevel, userId, organizationId,
                searchTerm, startDate, endDate, pageable);
        return page.map(mapper::toDTO);
    }

    /**
     * Get log statistics by log level for a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return map of log level to count
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getLogStatsByLevel(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = repository.countByLogLevelAndDateRange(startDate, endDate);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * Find logs by HTTP status code range
     * @param minStatus minimum status code (inclusive)
     * @param maxStatus maximum status code (exclusive)
     * @param startDate the start date
     * @param pageable pagination information
     * @return page of matching logs
     */
    @Transactional(readOnly = true)
    public Page<LogsSystemDTO> findByResponseStatusRange(Integer minStatus,
                                                         Integer maxStatus,
                                                         LocalDateTime startDate,
                                                         Pageable pageable) {
        Page<LogsSystem> page = repository.findByResponseStatusRange(minStatus, maxStatus, startDate, pageable);
        return page.map(mapper::toDTO);
    }

    /**
     * Delete logs older than a specified date
     * @param cutoffDate the cutoff date
     * @return number of deleted logs
     */
    @Transactional
    public void deleteOldLogs(LocalDateTime cutoffDate) {
        logger.info("Deleting logs older than: {}", cutoffDate);
        repository.deleteLogsOlderThan(cutoffDate);
    }

    /**
     * Create a log entry for an exception
     * @param serviceName the service name
     * @param exception the exception to log
     * @param userId optional user ID
     * @param organizationId optional organization ID
     * @return the created log entry
     */
    public LogsSystemDTO logException(String serviceName,
                                      Exception exception,
                                      Integer userId,
                                      Integer organizationId) {
        LogsSystemDTO log = LogsSystemDTO.builder()
                .serviceName(serviceName)
                .logLevel(LogLevel.ERROR.toString())
                .message(exception.getMessage())
                .stackTrace(getStackTraceAsString(exception))
                .userId(userId)
                .organizationId(organizationId)
                .timestamp(LocalDateTime.now())
                .build();

        return saveLog(log);
    }

    /**
     * Convert exception stack trace to string
     * @param exception the exception
     * @return stack trace as string
     */
    private String getStackTraceAsString(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception.getClass().getName()).append(": ").append(exception.getMessage()).append("\n");

        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }

        // Handle nested cause
        Throwable cause = exception.getCause();
        if (cause != null) {
            sb.append("Caused by: ").append(cause.getClass().getName())
                    .append(": ").append(cause.getMessage()).append("\n");
            for (StackTraceElement element : cause.getStackTrace()) {
                sb.append("\tat ").append(element.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Find recent logs for a specific service and environment
     * @param serviceName the service name
     * @param environment the environment
     * @param hours how many hours back to look
     * @return list of recent logs
     */
    @Transactional(readOnly = true)
    public List<LogsSystemDTO> findRecentLogsByServiceAndEnvironment(String serviceName,
                                                                     String environment,
                                                                     int hours) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(hours);
        List<LogsSystem> logs = repository.findByServiceAndEnvironment(serviceName, environment, startDate);
        return mapper.toDTOList(logs);
    }
}