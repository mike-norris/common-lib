package com.openrangelabs.middleware.logging.service;

import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.mapper.LogsUserMapper;
import com.openrangelabs.middleware.logging.model.LogsUser;
import com.openrangelabs.middleware.logging.model.LogsUserId;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.logging.repository.LogsUserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogsUserServiceTest {

    @Mock
    private LogsUserRepository repository;

    @Mock
    private LogsUserMapper mapper;

    @InjectMocks
    private LogsUserService service;

    private LogsUser testEntity;
    private LogsUserDTO testDTO;
    private LogsUserId testId;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testId = LogsUserId.builder()
                .userId(123)
                .createdDt(now)
                .build();

        testEntity = LogsUser.builder()
                .id(testId)
                .organizationId(456)
                .type(UserLogType.LOGIN.getCode())
                .description("Test login")
                .build();

        testDTO = LogsUserDTO.builder()
                .userId(123)
                .createdDt(now)
                .organizationId(456)
                .type(UserLogType.LOGIN.getCode())
                .description("Test login")
                .build();
    }

    @Test
    void testSaveLog() {
        // Given
        when(mapper.toEntity(any(LogsUserDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsUser.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsUser.class))).thenReturn(testDTO);

        // When
        LogsUserDTO result = service.saveLog(testDTO);

        // Then
        assertNotNull(result);
        assertEquals(testDTO.getUserId(), result.getUserId());
        assertEquals(testDTO.getType(), result.getType());
        verify(repository).save(any(LogsUser.class));
    }

    @Test
    void testSaveLogWithInvalidType() {
        // Given
        testDTO.setType("INVALID_TYPE");

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.saveLog(testDTO);
        });

        // Verify the cause is IllegalArgumentException
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertTrue(exception.getCause().getMessage().contains("Invalid log type"));
    }

    @Test
    void testSaveLogsBatch() {
        // Given
        List<LogsUserDTO> dtoList = Arrays.asList(testDTO);
        List<LogsUser> entityList = Arrays.asList(testEntity);

        when(mapper.toEntityList(anyList())).thenReturn(entityList);
        when(repository.saveAll(anyList())).thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsUserDTO> result = service.saveLogsBatch(dtoList);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).saveAll(anyList());
    }

    @Test
    void testFindById() {
        // Given
        when(repository.findById(any(LogsUserId.class))).thenReturn(Optional.of(testEntity));
        when(mapper.toDTO(any(LogsUser.class))).thenReturn(testDTO);

        // When
        Optional<LogsUserDTO> result = service.findById(123, testId.getCreatedDt());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDTO.getUserId(), result.get().getUserId());
        verify(repository).findById(any(LogsUserId.class));
    }

    @Test
    void testFindByUserId() {
        // Given
        List<LogsUser> entityList = Arrays.asList(testEntity);
        List<LogsUserDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findAllByUserIdOrderByCreatedDtDesc(anyInt(), any(LocalDateTime.class)))
                .thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsUserDTO> result = service.findByUserId(123, null, false);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAllByUserIdOrderByCreatedDtDesc(eq(123), any(LocalDateTime.class));
    }

    @Test
    void testFindByOrganizationId() {
        // Given
        List<LogsUser> entityList = Arrays.asList(testEntity);
        List<LogsUserDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findAllByOrganizationIdOrderByCreatedDtAsc(anyInt(), any(LocalDateTime.class)))
                .thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsUserDTO> result = service.findByOrganizationId(456, null, true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAllByOrganizationIdOrderByCreatedDtAsc(eq(456), any(LocalDateTime.class));
    }

    @Test
    void testLogUserAction() {
        // Given
        when(mapper.toEntity(any(LogsUserDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsUser.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsUser.class))).thenReturn(testDTO);

        // When
        LogsUserDTO result = service.logUserAction(123, 456, UserLogType.CREATE.getCode(), "Created resource");

        // Then
        assertNotNull(result);
        verify(repository).save(any(LogsUser.class));
    }

    @Test
    void testLogLogin() {
        // Given
        when(mapper.toEntity(any(LogsUserDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsUser.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsUser.class))).thenReturn(testDTO);

        // When
        LogsUserDTO result = service.logLogin(123, 456, "IP: 192.168.1.1");

        // Then
        assertNotNull(result);
        verify(repository).save(any(LogsUser.class));
    }

    @Test
    void testLogLogout() {
        // Given
        when(mapper.toEntity(any(LogsUserDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsUser.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsUser.class))).thenReturn(testDTO);

        // When
        LogsUserDTO result = service.logLogout(123, 456, "Session ended");

        // Then
        assertNotNull(result);
        verify(repository).save(any(LogsUser.class));
    }

    @Test
    void testCountUserLogsByType() {
        // Given
        List<LogsUser> logs = Arrays.asList(
                LogsUser.builder()
                        .id(testId)
                        .organizationId(456)
                        .type(UserLogType.LOGIN.getCode())
                        .build(),
                LogsUser.builder()
                        .id(LogsUserId.builder().userId(123).createdDt(LocalDateTime.now()).build())
                        .organizationId(456)
                        .type(UserLogType.LOGIN.getCode())
                        .build(),
                LogsUser.builder()
                        .id(LogsUserId.builder().userId(123).createdDt(LocalDateTime.now()).build())
                        .organizationId(456)
                        .type(UserLogType.LOGOUT.getCode())
                        .build()
        );

        when(repository.findAllByUserIdOrderByCreatedDtDesc(anyInt(), any(LocalDateTime.class)))
                .thenReturn(logs);

        // When
        List<Object[]> result = service.countUserLogsByType(
                123, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // LOGIN and LOGOUT types
    }

    @Test
    void testDeleteOldLogs() {
        // Given
        List<LogsUser> oldLogs = Arrays.asList(testEntity);
        when(repository.findAll()).thenReturn(oldLogs);

        // When
        long deletedCount = service.deleteOldLogs(LocalDateTime.now().plusDays(1));

        // Then
        assertEquals(1, deletedCount);
        verify(repository).deleteAll(anyList());
    }

    @Test
    void testSaveLogWithException() {
        // Given
        when(mapper.toEntity(any(LogsUserDTO.class))).thenThrow(new RuntimeException("Mapping error"));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            service.saveLog(testDTO);
        });
    }

    @Test
    void testValidationErrors() {
        // Given - DTO with invalid data
        LogsUserDTO invalidDTO = LogsUserDTO.builder()
                .userId(null)  // Required field
                .organizationId(-1)  // Must be positive
                .type("INVALID")  // Invalid type
                .description("A".repeat(300))  // Exceeds max length
                .build();

        // When/Then - Various validation scenarios
        assertThrows(RuntimeException.class, () -> {
            service.saveLog(invalidDTO);
        });
    }

    @Test
    void testFindByUserIdWithCustomDateLimit() {
        // Given
        LocalDateTime customDate = LocalDateTime.now().minusDays(30);
        List<LogsUser> entityList = Arrays.asList(testEntity);
        List<LogsUserDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findAllByUserIdOrderByCreatedDtAsc(anyInt(), any(LocalDateTime.class)))
                .thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsUserDTO> result = service.findByUserId(123, customDate, true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAllByUserIdOrderByCreatedDtAsc(123, customDate);
    }

    @Test
    void testLogUserActionWithNullDescription() {
        // Given
        when(mapper.toEntity(any(LogsUserDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsUser.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsUser.class))).thenReturn(testDTO);

        // When
        LogsUserDTO result = service.logUserAction(123, 456, UserLogType.VIEW.getCode(), null);

        // Then
        assertNotNull(result);
        verify(repository).save(any(LogsUser.class));
    }
}