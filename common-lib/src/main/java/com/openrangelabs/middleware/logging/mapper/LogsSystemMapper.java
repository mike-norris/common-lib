package com.openrangelabs.middleware.logging.mapper;

import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.model.LogsSystem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between LogsSystem entities and DTOs
 */
@Component
public class LogsSystemMapper {

    /**
     * Convert LogsSystem entity to DTO
     * @param entity the LogsSystem entity
     * @return the corresponding DTO
     */
    public LogsSystemDTO toDTO(LogsSystem entity) {
        if (entity == null) {
            return null;
        }

        return LogsSystemDTO.builder()
                .id(entity.getId())
                .timestamp(entity.getTimestamp())
                .serviceName(entity.getServiceName())
                .hostName(entity.getHostName())
                .logLevel(entity.getLogLevel())
                .loggerName(entity.getLoggerName())
                .threadName(entity.getThreadName())
                .message(entity.getMessage())
                .stackTrace(entity.getStackTrace())
                .mdcData(entity.getMdcData())
                .correlationId(entity.getCorrelationId())
                .userId(entity.getUserId())
                .organizationId(entity.getOrganizationId())
                .requestUri(entity.getRequestUri())
                .requestMethod(entity.getRequestMethod())
                .responseStatus(entity.getResponseStatus())
                .executionTimeMs(entity.getExecutionTimeMs())
                .environment(entity.getEnvironment())
                .version(entity.getVersion())
                .build();
    }

    /**
     * Convert LogsSystemDTO to entity
     * @param dto the LogsSystemDTO
     * @return the corresponding entity
     */
    public LogsSystem toEntity(LogsSystemDTO dto) {
        if (dto == null) {
            return null;
        }

        LogsSystem entity = LogsSystem.builder()
                .serviceName(dto.getServiceName())
                .hostName(dto.getHostName())
                .logLevel(dto.getLogLevel())
                .loggerName(dto.getLoggerName())
                .threadName(dto.getThreadName())
                .message(dto.getMessage())
                .stackTrace(dto.getStackTrace())
                .mdcData(dto.getMdcData())
                .correlationId(dto.getCorrelationId())
                .userId(dto.getUserId())
                .organizationId(dto.getOrganizationId())
                .requestUri(dto.getRequestUri())
                .requestMethod(dto.getRequestMethod())
                .responseStatus(dto.getResponseStatus())
                .executionTimeMs(dto.getExecutionTimeMs())
                .environment(dto.getEnvironment())
                .version(dto.getVersion())
                .build();

        // Set id and timestamp if provided
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        if (dto.getTimestamp() != null) {
            entity.setTimestamp(dto.getTimestamp());
        }

        return entity;
    }

    /**
     * Convert a list of entities to DTOs
     * @param entities the list of entities
     * @return the list of DTOs
     */
    public List<LogsSystemDTO> toDTOList(List<LogsSystem> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of DTOs to entities
     * @param dtos the list of DTOs
     * @return the list of entities
     */
    public List<LogsSystem> toEntityList(List<LogsSystemDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing entity with values from a DTO
     * @param dto the source DTO
     * @param entity the target entity to update
     */
    public void updateEntityFromDTO(LogsSystemDTO dto, LogsSystem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getServiceName() != null) {
            entity.setServiceName(dto.getServiceName());
        }
        if (dto.getHostName() != null) {
            entity.setHostName(dto.getHostName());
        }
        if (dto.getLogLevel() != null) {
            entity.setLogLevel(dto.getLogLevel());
        }
        if (dto.getLoggerName() != null) {
            entity.setLoggerName(dto.getLoggerName());
        }
        if (dto.getThreadName() != null) {
            entity.setThreadName(dto.getThreadName());
        }
        if (dto.getMessage() != null) {
            entity.setMessage(dto.getMessage());
        }
        if (dto.getStackTrace() != null) {
            entity.setStackTrace(dto.getStackTrace());
        }
        if (dto.getMdcData() != null) {
            entity.setMdcData(dto.getMdcData());
        }
        if (dto.getCorrelationId() != null) {
            entity.setCorrelationId(dto.getCorrelationId());
        }
        if (dto.getUserId() != null) {
            entity.setUserId(dto.getUserId());
        }
        if (dto.getOrganizationId() != null) {
            entity.setOrganizationId(dto.getOrganizationId());
        }
        if (dto.getRequestUri() != null) {
            entity.setRequestUri(dto.getRequestUri());
        }
        if (dto.getRequestMethod() != null) {
            entity.setRequestMethod(dto.getRequestMethod());
        }
        if (dto.getResponseStatus() != null) {
            entity.setResponseStatus(dto.getResponseStatus());
        }
        if (dto.getExecutionTimeMs() != null) {
            entity.setExecutionTimeMs(dto.getExecutionTimeMs());
        }
        if (dto.getEnvironment() != null) {
            entity.setEnvironment(dto.getEnvironment());
        }
        if (dto.getVersion() != null) {
            entity.setVersion(dto.getVersion());
        }
    }
}