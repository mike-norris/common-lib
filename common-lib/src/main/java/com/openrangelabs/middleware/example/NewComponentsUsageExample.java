package com.openrangelabs.middleware.example;

import com.openrangelabs.middleware.messaging.dto.UserCreationEventDTO;
import com.openrangelabs.middleware.user.dto.PortalUserDTO;
import com.openrangelabs.middleware.user.mapper.PortalUserMapper;
import com.openrangelabs.middleware.user.model.PortalUser;
import com.openrangelabs.middleware.user.repository.PortalUserRepository;
import com.openrangelabs.middleware.exception.dto.ErrorResponseDTO;
import com.openrangelabs.middleware.exception.dto.ValidationErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating usage of the new DTO and Mapper components
 */
@Component
public class NewComponentsUsageExample {

    @Autowired
    private PortalUserRepository portalUserRepository;

    @Autowired
    private PortalUserMapper portalUserMapper;

    /**
     * Example of creating and saving a portal user
     */
    public PortalUserDTO createPortalUser() {
        // Create a PortalUserDTO using builder pattern
        PortalUserDTO newUser = PortalUserDTO.builder()
                .userId(12345)
                .organizationId(100)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .status("PENDING")
                .operationType("CREATE")
                .createdBy("admin")
                .notes("User created via portal")
                .build();

        // Convert to entity and save
        PortalUser entity = portalUserMapper.toEntity(newUser);
        PortalUser saved = portalUserRepository.save(entity);

        // Convert back to DTO and return
        return portalUserMapper.toDTO(saved);
    }

    /**
     * Example of creating a user creation event for messaging
     */
    public UserCreationEventDTO createUserCreationEvent() {
        // Create an event DTO for messaging
        UserCreationEventDTO event = UserCreationEventDTO.builder()
                .eventType("USER_CREATED")
                .userId(12345)
                .organizationId(100)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .status("ACTIVE")
                .triggeredBy("admin")
                .sourceService("user-management-service")
                .additionalData("{\"source\":\"portal\",\"region\":\"us-east-1\"}")
                .build();

        // This event can now be serialized and sent to the CREATE_USER exchange
        System.out.println("Event created: " + event);
        return event;
    }

    /**
     * Example of updating a portal user
     */
    public PortalUserDTO updatePortalUser(Long userId, String newEmail) {
        // Find existing user
        PortalUser existingUser = portalUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create update DTO using mapper convenience method
        PortalUserDTO updateEvent = portalUserMapper.createUserUpdateDTO(existingUser, "admin");
        updateEvent.setEmail(newEmail);
        updateEvent.setOperationType("UPDATE");

        // Save the update event
        PortalUser entity = portalUserMapper.toEntity(updateEvent);
        PortalUser saved = portalUserRepository.save(entity);

        return portalUserMapper.toDTO(saved);
    }

    /**
     * Example of querying portal users
     */
    public void queryPortalUsers() {
        // Find by organization
        List<PortalUser> orgUsers = portalUserRepository.findByOrganizationId(100);
        List<PortalUserDTO> orgUserDTOs = portalUserMapper.toDTOList(orgUsers);

        // Find recent events (last 7 days)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<PortalUser> recentEvents = portalUserRepository.findRecentEvents(sevenDaysAgo);

        // Search with multiple criteria
        var searchResults = portalUserRepository.searchPortalUsers(
                100,                    // organizationId
                "ACTIVE",              // status
                "CREATE",              // operationType
                "john",                // username search
                null,                  // email search
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                org.springframework.data.domain.PageRequest.of(0, 20)
        );

        System.out.println("Found " + searchResults.getTotalElements() + " matching users");
    }

    /**
     * Example of using the error response DTOs
     */
    public void demonstrateErrorResponses() {
        // Create a validation error response
        Map<String, String> validationErrors = Map.of(
                "username", "Username is required",
                "email", "Email format is invalid"
        );

        ValidationErrorResponseDTO validationResponse = ValidationErrorResponseDTO.builder()
                .message("Validation failed")
                .errors(validationErrors)
                .path("/api/v1/users")
                .status(400)
                .build();

        // Create a general error response
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .message("User not found")
                .path("/api/v1/users/12345")
                .status(404)
                .error("Not Found")
                .build();

        System.out.println("Validation error: " + validationResponse);
        System.out.println("General error: " + errorResponse);
    }

    /**
     * Example of batch operations
     */
    public void batchOperations() {
        // Create multiple users
        List<PortalUserDTO> newUsers = List.of(
                PortalUserDTO.builder()
                        .userId(12346)
                        .organizationId(100)
                        .username("jane.doe")
                        .email("jane.doe@example.com")
                        .operationType("CREATE")
                        .build(),
                PortalUserDTO.builder()
                        .userId(12347)
                        .organizationId(100)
                        .username("bob.smith")
                        .email("bob.smith@example.com")
                        .operationType("CREATE")
                        .build()
        );

        // Convert to entities and save all
        List<PortalUser> entities = portalUserMapper.toEntityList(newUsers);
        List<PortalUser> saved = portalUserRepository.saveAll(entities);

        // Convert back to DTOs
        List<PortalUserDTO> savedDTOs = portalUserMapper.toDTOList(saved);
        System.out.println("Saved " + savedDTOs.size() + " users");
    }

    /**
     * Example of getting statistics
     */
    public void getStatistics() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Get status counts for organization
        List<Object[]> statusCounts = portalUserRepository
                .countByStatusAndOrganization(100, thirtyDaysAgo);

        statusCounts.forEach(row -> {
            String status = (String) row[0];
            Long count = (Long) row[1];
            System.out.println(status + ": " + count + " users");
        });

        // Get operation type counts
        List<Object[]> operationCounts = portalUserRepository
                .countByOperationTypeAndOrganization(100, thirtyDaysAgo);

        operationCounts.forEach(row -> {
            String operation = (String) row[0];
            Long count = (Long) row[1];
            System.out.println(operation + ": " + count + " operations");
        });
    }

    /**
     * Example of partial updates using the mapper
     */
    public void partialUpdate() {
        // Find existing user
        PortalUser existingUser = portalUserRepository.findByUserId(12345)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create partial update DTO
        PortalUserDTO partialUpdate = PortalUserDTO.builder()
                .status("ACTIVE")
                .notes("User activated by admin")
                .operationType("ACTIVATE")
                .build();

        // Apply partial update
        portalUserMapper.updateEntityFromDTO(partialUpdate, existingUser);

        // Save updated entity
        PortalUser saved = portalUserRepository.save(existingUser);
        System.out.println("Updated user: " + saved);
    }

    /**
     * Example of creating events for different operations
     */
    public void createDifferentEventTypes() {
        // User creation event
        UserCreationEventDTO createEvent = UserCreationEventDTO.builder()
                .eventType("USER_CREATED")
                .userId(12345)
                .organizationId(100)
                .username("new.user")
                .email("new.user@example.com")
                .status("PENDING")
                .sourceService("user-service")
                .build();

        // User activation event
        UserCreationEventDTO activateEvent = UserCreationEventDTO.builder()
                .eventType("USER_ACTIVATED")
                .userId(12345)
                .organizationId(100)
                .username("existing.user")
                .email("existing.user@example.com")
                .status("ACTIVE")
                .sourceService("admin-service")
                .triggeredBy("admin")
                .build();

        // User deletion event
        UserCreationEventDTO deleteEvent = UserCreationEventDTO.builder()
                .eventType("USER_DELETED")
                .userId(12345)
                .organizationId(100)
                .username("deleted.user")
                .email("deleted.user@example.com")
                .status("INACTIVE")
                .sourceService("cleanup-service")
                .additionalData("{\"reason\":\"account_closure\",\"retention_days\":30}")
                .build();

        System.out.println("Create event: " + createEvent);
        System.out.println("Activate event: " + activateEvent);
        System.out.println("Delete event: " + deleteEvent);
    }

    /**
     * Example of data validation and error handling
     */
    public void demonstrateValidation() {
        try {
            // This will fail validation - missing required fields
            PortalUserDTO invalidUser = PortalUserDTO.builder()
                    .username("test") // missing userId, organizationId, email, operationType
                    .build();

            PortalUser entity = portalUserMapper.toEntity(invalidUser);
            portalUserRepository.save(entity); // This would trigger validation errors

        } catch (Exception e) {
            // Create appropriate error response
            ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                    .message("User validation failed: " + e.getMessage())
                    .status(400)
                    .error("Bad Request")
                    .build();

            System.err.println("Validation error: " + errorResponse);
        }
    }

    /**
     * Example of checking for duplicates before creation
     */
    public PortalUserDTO createUserSafely(String username, String email, Integer organizationId) {
        // Check for duplicates
        if (portalUserRepository.existsByUsernameAndOrganizationId(username, organizationId)) {
            throw new IllegalArgumentException("Username already exists in organization");
        }

        if (portalUserRepository.existsByEmailAndOrganizationId(email, organizationId)) {
            throw new IllegalArgumentException("Email already exists in organization");
        }

        // Create user if no duplicates
        PortalUserDTO newUser = PortalUserDTO.builder()
                .userId(generateUserId()) // Assume this method exists
                .organizationId(organizationId)
                .username(username)
                .email(email)
                .status("PENDING")
                .operationType("CREATE")
                .createdBy("system")
                .build();

        PortalUser entity = portalUserMapper.toEntity(newUser);
        PortalUser saved = portalUserRepository.save(entity);

        return portalUserMapper.toDTO(saved);
    }

    /**
     * Example of cleanup operations
     */
    public void cleanupOldData() {
        // Delete portal user events older than 1 year
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        portalUserRepository.deleteEventsOlderThan(oneYearAgo);
        System.out.println("Cleaned up portal user events older than " + oneYearAgo);
    }

    /**
     * Helper method to demonstrate user ID generation
     */
    private Integer generateUserId() {
        // In a real application, this would come from a sequence or UUID conversion
        return (int) (System.currentTimeMillis() % 1000000);
    }

    /**
     * Example of using enum constants with the new DTOs
     */
    public void demonstrateEnumIntegration() {
        // Use queue names from enum
        String queueName = com.openrangelabs.middleware.messaging.QueueName.PORTAL_USER.getQueueName();
        String exchangeName = com.openrangelabs.middleware.messaging.ExchangeName.CREATE_USER.getExchangeName();

        System.out.println("Portal users are processed via queue: " + queueName);
        System.out.println("User creation events are sent to exchange: " + exchangeName);

        // Create event that would be sent to these queues/exchanges
        UserCreationEventDTO event = UserCreationEventDTO.builder()
                .eventType("USER_CREATED")
                .userId(12345)
                .organizationId(100)
                .username("queue.user")
                .email("queue.user@example.com")
                .sourceService("portal-service")
                .build();

        // In a real application, this event would be sent to RabbitMQ
        System.out.println("Event ready for messaging: " + event);
    }
}