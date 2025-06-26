package com.openrangelabs.middleware.logging.service;

import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.mapper.LogsSystemMapper;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.LogsSystem;
import com.openrangelabs.middleware.logging.repository.LogsSystemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogsSystemServiceTest {

    @Mock
    private LogsSystemRepository repository;

    @Mock
    private LogsSystemMapper mapper;

    @InjectMocks
    private LogsSystemService service;

    private LogsSystem testEntity;
    private LogsSystemDTO testDTO;

    @BeforeEach
    void setUp() {
        testEntity = LogsSystem.builder()
                .serviceName("test-service")
                .logLevel(LogLevel.INFO.toString())
                .message("Test message")
                .timestamp(LocalDateTime.now())
                .build();
        testEntity.setId(1L);

        testDTO = LogsSystemDTO.builder()
                .id(1L)
                .serviceName("test-service")
                .logLevel(LogLevel.INFO.toString())
                .message("Test message")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void testSaveLog() {
        // Given
        when(mapper.toEntity(any(LogsSystemDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsSystem.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsSystem.class))).thenReturn(testDTO);

        // When
        LogsSystemDTO result = service.saveLog(testDTO);

        // Then
        assertNotNull(result);
        assertEquals(testDTO.getServiceName(), result.getServiceName());
        verify(repository).save(any(LogsSystem.class));
    }

    @Test
    void testSaveLogsBatch() {
        // Given
        List<LogsSystemDTO> dtoList = Arrays.asList(testDTO);
        List<LogsSystem> entityList = Arrays.asList(testEntity);

        when(mapper.toEntityList(anyList())).thenReturn(entityList);
        when(repository.saveAll(anyList())).thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsSystemDTO> result = service.saveLogsBatch(dtoList);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).saveAll(anyList());
    }

    @Test
    void testFindByServiceAndDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<LogsSystem> entityList = Arrays.asList(testEntity);
        List<LogsSystemDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findByServiceNameAndDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsSystemDTO> result = service.findByServiceAndDateRange("test-service", startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findByServiceNameAndDateRange("test-service", startDate, endDate);
    }

    @Test
    void testFindByLogLevel() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LogsSystem> entityPage = new PageImpl<>(Arrays.asList(testEntity));

        when(repository.findByLogLevel(anyString(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(entityPage);
        when(mapper.toDTO(any(LogsSystem.class))).thenReturn(testDTO);

        // When
        Page<LogsSystemDTO> result = service.findByLogLevel("INFO", LocalDateTime.now().minusDays(1), pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByLogLevel(eq("INFO"), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void testFindByCorrelationId() {
        // Given
        String correlationId = "test-correlation-id";
        List<LogsSystem> entityList = Arrays.asList(testEntity);
        List<LogsSystemDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findByCorrelationIdOrderByTimestampAsc(anyString())).thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsSystemDTO> result = service.findByCorrelationId(correlationId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findByCorrelationIdOrderByTimestampAsc(correlationId);
    }

    @Test
    void testFindSlowRequests() {
        // Given
        Long threshold = 1000L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        List<LogsSystem> entityList = Arrays.asList(testEntity);
        List<LogsSystemDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findSlowRequests(anyLong(), any(LocalDateTime.class))).thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsSystemDTO> result = service.findSlowRequests(threshold, startDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findSlowRequests(threshold, startDate);
    }

    @Test
    void testGetLogStatsByLevel() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<Object[]> stats = Arrays.asList(
                new Object[]{"INFO", 100L},
                new Object[]{"ERROR", 50L},
                new Object[]{"WARN", 25L}
        );

        when(repository.countByLogLevelAndDateRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(stats);

        // When
        Map<String, Long> result = service.getLogStatsByLevel(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(100L, result.get("INFO"));
        assertEquals(50L, result.get("ERROR"));
        assertEquals(25L, result.get("WARN"));
        verify(repository).countByLogLevelAndDateRange(startDate, endDate);
    }

    @Test
    void testLogException() {
        // Given
        String serviceName = "test-service";
        Exception exception = new RuntimeException("Test exception");
        Integer userId = 123;
        Integer organizationId = 456;

        when(mapper.toEntity(any(LogsSystemDTO.class))).thenReturn(testEntity);
        when(repository.save(any(LogsSystem.class))).thenReturn(testEntity);
        when(mapper.toDTO(any(LogsSystem.class))).thenReturn(testDTO);

        // When
        LogsSystemDTO result = service.logException(serviceName, exception, userId, organizationId);

        // Then
        assertNotNull(result);
        verify(repository).save(any(LogsSystem.class));
    }

    @Test
    void testDeleteOldLogs() {
        // Given
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

        // When
        service.deleteOldLogs(cutoffDate);

        // Then
        verify(repository).deleteLogsOlderThan(cutoffDate);
    }

    @Test
    void testSearchLogs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LogsSystem> entityPage = new PageImpl<>(Arrays.asList(testEntity));

        when(repository.searchLogs(
                any(String.class), any(String.class), any(Integer.class), any(Integer.class),
                any(String.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(entityPage);
        when(mapper.toDTO(any(LogsSystem.class))).thenReturn(testDTO);

        // When
        Page<LogsSystemDTO> result = service.searchLogs(
                "test-service", "INFO", 123, 456, "search term",
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).searchLogs(
                any(String.class), any(String.class), any(Integer.class), any(Integer.class),
                any(String.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testInvalidLogLevel() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.findByLogLevel("INVALID_LEVEL", LocalDateTime.now(), pageable);
        });
    }

    @Test
    void testFindRecentLogsByServiceAndEnvironment() {
        // Given
        String serviceName = "test-service";
        String environment = "production";
        int hours = 24;
        List<LogsSystem> entityList = Arrays.asList(testEntity);
        List<LogsSystemDTO> dtoList = Arrays.asList(testDTO);

        when(repository.findByServiceAndEnvironment(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(entityList);
        when(mapper.toDTOList(anyList())).thenReturn(dtoList);

        // When
        List<LogsSystemDTO> result = service.findRecentLogsByServiceAndEnvironment(
                serviceName, environment, hours);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findByServiceAndEnvironment(eq(serviceName), eq(environment), any(LocalDateTime.class));
    }

    @Test
    void testFindErrorLogs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<LogsSystem> entityPage = new PageImpl<>(Arrays.asList(testEntity));

        when(repository.findLogsWithErrors(any(LocalDateTime.class), any(Pageable.class))).thenReturn(entityPage);
        when(mapper.toDTO(any(LogsSystem.class))).thenReturn(testDTO);

        // When
        Page<LogsSystemDTO> result = service.findErrorLogs(LocalDateTime.now().minusDays(1), pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findLogsWithErrors(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void testFindByResponseStatusRange() {
        // Given
        Integer minStatus = 400;
        Integer maxStatus = 500;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<LogsSystem> entityPage = new PageImpl<>(Arrays.asList(testEntity));

        when(repository.findByResponseStatusRange(anyInt(), anyInt(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(entityPage);
        when(mapper.toDTO(any(LogsSystem.class))).thenReturn(testDTO);

        // When
        Page<LogsSystemDTO> result = service.findByResponseStatusRange(minStatus, maxStatus, startDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByResponseStatusRange(minStatus, maxStatus, startDate, pageable);
    }

    @Test
    void testSaveLogWithException() {
        // Given
        when(mapper.toEntity(any(LogsSystemDTO.class))).thenThrow(new RuntimeException("Mapping error"));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            service.saveLog(testDTO);
        });
    }

    @Test
    void testSaveLogsBatchWithException() {
        // Given
        List<LogsSystemDTO> dtoList = Arrays.asList(testDTO);
        when(mapper.toEntityList(anyList())).thenThrow(new RuntimeException("Batch mapping error"));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            service.saveLogsBatch(dtoList);
        });
    }
}