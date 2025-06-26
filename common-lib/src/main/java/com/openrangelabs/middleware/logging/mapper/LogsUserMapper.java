package com.openrangelabs.middleware.logging.mapper;

import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.model.LogsUser;
import com.openrangelabs.middleware.logging.model.LogsUserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between LogsUser entities and DTOs
 */
@Component
public class LogsUserMapper {

    /**
     * Convert LogsUser entity to DTO
     * @param entity the LogsUser entity
     * @return the corresponding DTO
     */
    public LogsUserDTO toDTO(LogsUser entity) {
        if (entity == null) {
            return null;
        }

        return LogsUserDTO.builder()
                .userId(entity.getId() != null ? entity.getId().getUserId() : null)
                .createdDt(entity.getId() != null ? entity.getId().getCreatedDt() : null)
                .organizationId(entity.getOrganizationId())
                .description(entity.getDescription())
                .type(entity.getType())
                .build();
    }

    /**
     * Convert LogsUserDTO to entity
     * @param dto the LogsUserDTO
     * @return the corresponding entity
     */
    public LogsUser toEntity(LogsUserDTO dto) {
        if (dto == null) {
            return null;
        }

        LogsUserId id = LogsUserId.builder()
                .userId(dto.getUserId())
                .createdDt(dto.getCreatedDt())
                .build();

        return LogsUser.builder()
                .id(id)
                .organizationId(dto.getOrganizationId())
                .description(dto.getDescription())
                .type(dto.getType())
                .build();
    }

    /**
     * Convert a list of entities to DTOs
     * @param entities the list of entities
     * @return the list of DTOs
     */
    public List<LogsUserDTO> toDTOList(List<LogsUser> entities) {
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
    public List<LogsUser> toEntityList(List<LogsUserDTO> dtos) {
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
    public void updateEntityFromDTO(LogsUserDTO dto, LogsUser entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Update composite key if both parts are provided
        if (dto.getUserId() != null && dto.getCreatedDt() != null) {
            LogsUserId id = LogsUserId.builder()
                    .userId(dto.getUserId())
                    .createdDt(dto.getCreatedDt())
                    .build();
            entity.setId(id);
        }

        if (dto.getOrganizationId() != null) {
            entity.setOrganizationId(dto.getOrganizationId());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
    }
}