package com.openrangelabs.middleware.user.mapper;

import com.openrangelabs.middleware.user.dto.PortalUserDTO;
import com.openrangelabs.middleware.user.model.PortalUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between PortalUser entities and DTOs
 */
@Component
public class PortalUserMapper {

    /**
     * Convert PortalUser entity to DTO
     * @param entity the PortalUser entity
     * @return the corresponding DTO
     */
    public PortalUserDTO toDTO(PortalUser entity) {
        if (entity == null) {
            return null;
        }

        return PortalUserDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .organizationId(entity.getOrganizationId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .status(entity.getStatus())
                .operationType(entity.getOperationType())
                .createdDt(entity.getCreatedDt())
                .createdBy(entity.getCreatedBy())
                .notes(entity.getNotes())
                .build();
    }

    /**
     * Convert PortalUserDTO to entity
     * @param dto the PortalUserDTO
     * @return the corresponding entity
     */
    public PortalUser toEntity(PortalUserDTO dto) {
        if (dto == null) {
            return null;
        }

        PortalUser entity = PortalUser.builder()
                .userId(dto.getUserId())
                .organizationId(dto.getOrganizationId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .status(dto.getStatus())
                .operationType(dto.getOperationType())
                .createdDt(dto.getCreatedDt())
                .createdBy(dto.getCreatedBy())
                .notes(dto.getNotes())
                .build();

        // Set ID if provided (for updates)
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }

        return entity;
    }

    /**
     * Convert a list of entities to DTOs
     * @param entities the list of entities
     * @return the list of DTOs
     */
    public List<PortalUserDTO> toDTOList(List<PortalUser> entities) {
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
    public List<PortalUser> toEntityList(List<PortalUserDTO> dtos) {
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
    public void updateEntityFromDTO(PortalUserDTO dto, PortalUser entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserId() != null) {
            entity.setUserId(dto.getUserId());
        }
        if (dto.getOrganizationId() != null) {
            entity.setOrganizationId(dto.getOrganizationId());
        }
        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getOperationType() != null) {
            entity.setOperationType(dto.getOperationType());
        }
        if (dto.getCreatedBy() != null) {
            entity.setCreatedBy(dto.getCreatedBy());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        // Note: createdDt is typically not updated after creation
    }

    /**
     * Create a DTO for user creation event
     * @param userId the user ID
     * @param organizationId the organization ID
     * @param username the username
     * @param email the email
     * @param createdBy who created the user
     * @return PortalUserDTO configured for creation
     */
    public PortalUserDTO createUserCreationDTO(Integer userId, Integer organizationId,
                                               String username, String email, String createdBy) {
        return PortalUserDTO.builder()
                .userId(userId)
                .organizationId(organizationId)
                .username(username)
                .email(email)
                .status("PENDING")
                .operationType("CREATE")
                .createdBy(createdBy)
                .build();
    }

    /**
     * Create a DTO for user update event
     * @param existingUser the existing user data
     * @param updatedBy who updated the user
     * @return PortalUserDTO configured for update
     */
    public PortalUserDTO createUserUpdateDTO(PortalUser existingUser, String updatedBy) {
        if (existingUser == null) {
            return null;
        }

        return PortalUserDTO.builder()
                .id(existingUser.getId())
                .userId(existingUser.getUserId())
                .organizationId(existingUser.getOrganizationId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .status(existingUser.getStatus())
                .operationType("UPDATE")
                .createdBy(updatedBy)
                .build();
    }
}