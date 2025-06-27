package com.openrangelabs.middleware.enums;

import com.openrangelabs.middleware.config.Environment;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.messaging.ExchangeName;
import com.openrangelabs.middleware.messaging.QueueName;
import com.openrangelabs.middleware.web.HttpMethod;
import com.openrangelabs.middleware.web.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTests {

    @Test
    void testQueueNameEnum() {
        // Test queue name retrieval
        assertEquals("q.logs-user", QueueName.USER_LOGS.getQueueName());
        assertEquals("q.logs-user-dlq", QueueName.USER_LOGS_DLQ.getQueueName());

        // Test from string
        assertEquals(QueueName.USER_LOGS, QueueName.fromQueueName("q.logs-user"));

        // Test invalid queue name
        assertThrows(IllegalArgumentException.class, () -> {
            QueueName.fromQueueName("invalid-queue");
        });

        // Test DLQ detection
        assertTrue(QueueName.USER_LOGS_DLQ.isDeadLetterQueue());
        assertFalse(QueueName.USER_LOGS.isDeadLetterQueue());

        // Test DLQ mapping
        assertEquals(QueueName.USER_LOGS_DLQ, QueueName.USER_LOGS.getDeadLetterQueue());
        assertNull(QueueName.USER_LOGS_DLQ.getDeadLetterQueue());
    }

    @Test
    void testExchangeNameEnum() {
        // Test exchange name retrieval
        assertEquals("x.logging", ExchangeName.LOGGING.getExchangeName());
        assertEquals("x.logging-dlx", ExchangeName.LOGGING_DLX.getExchangeName());

        // Test from string
        assertEquals(ExchangeName.LOGGING, ExchangeName.fromExchangeName("x.logging"));

        // Test DLX detection
        assertTrue(ExchangeName.LOGGING_DLX.isDeadLetterExchange());
        assertFalse(ExchangeName.LOGGING.isDeadLetterExchange());
    }

    @Test
    void testUserLogTypeEnum() {
        // Test code retrieval
        assertEquals("LOGIN", UserLogType.LOGIN.getCode());
        assertEquals("User login event", UserLogType.LOGIN.getDescription());

        // Test from code
        assertEquals(UserLogType.LOGIN, UserLogType.fromCode("LOGIN"));
        assertEquals(UserLogType.LOGIN, UserLogType.fromCode("login")); // Case insensitive

        // Test validation
        assertTrue(UserLogType.isValidCode("LOGIN"));
        assertFalse(UserLogType.isValidCode("INVALID"));
        assertFalse(UserLogType.isValidCode(null));
    }

    @Test
    void testLogLevelEnum() {
        // Test severity ordering
        assertTrue(LogLevel.ERROR.getSeverity() > LogLevel.WARN.getSeverity());
        assertTrue(LogLevel.WARN.getSeverity() > LogLevel.INFO.getSeverity());

        // Test isEnabledFor
        assertTrue(LogLevel.ERROR.isEnabledFor(LogLevel.INFO));
        assertTrue(LogLevel.ERROR.isEnabledFor(LogLevel.ERROR));
        assertFalse(LogLevel.INFO.isEnabledFor(LogLevel.WARN));

        // Test from string
        assertEquals(LogLevel.INFO, LogLevel.fromString("INFO"));
        assertEquals(LogLevel.INFO, LogLevel.fromString("info")); // Case insensitive
    }

    @Test
    void testHttpMethodEnum() {
        // Test method properties
        assertTrue(HttpMethod.GET.isIdempotent());
        assertTrue(HttpMethod.PUT.isIdempotent());
        assertFalse(HttpMethod.POST.isIdempotent());

        assertTrue(HttpMethod.POST.hasRequestBody());
        assertTrue(HttpMethod.PUT.hasRequestBody());
        assertFalse(HttpMethod.GET.hasRequestBody());

        // Test from string
        assertEquals(HttpMethod.GET, HttpMethod.fromString("GET"));
        assertEquals(HttpMethod.POST, HttpMethod.fromString("post")); // Case insensitive

        // Test validation
        assertTrue(HttpMethod.isValidMethod("GET"));
        assertFalse(HttpMethod.isValidMethod("INVALID"));
    }

    @Test
    void testHttpStatusEnum() {
        // Test status code properties
        assertEquals(200, HttpStatus.OK.getCode());
        assertEquals("OK", HttpStatus.OK.getReason());

        // Test status categories
        assertTrue(HttpStatus.OK.isSuccess());
        assertTrue(HttpStatus.CREATED.isSuccess());
        assertTrue(HttpStatus.BAD_REQUEST.isClientError());
        assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.isServerError());

        // Test from code
        assertEquals(HttpStatus.OK, HttpStatus.fromCode(200));
        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.fromCode(404));

        // Test category
        assertEquals("2xx Success", HttpStatus.OK.getCategory());
        assertEquals("4xx Client Error", HttpStatus.BAD_REQUEST.getCategory());

        // Test invalid code
        assertThrows(IllegalArgumentException.class, () -> {
            HttpStatus.fromCode(999);
        });
    }

    @Test
    void testEnvironmentEnum() {
        // Test environment properties
        assertEquals("production", Environment.PRODUCTION.getName());
        assertEquals("prod", Environment.PRODUCTION.getShortName());

        // Test from string - both full and short names
        assertEquals(Environment.PRODUCTION, Environment.fromString("production"));
        assertEquals(Environment.PRODUCTION, Environment.fromString("prod"));
        assertEquals(Environment.DEVELOPMENT, Environment.fromString("dev"));

        // Test environment categories
        assertTrue(Environment.PRODUCTION.isProductionLike());
        assertTrue(Environment.STAGING.isProductionLike());
        assertFalse(Environment.DEVELOPMENT.isProductionLike());

        assertTrue(Environment.DEVELOPMENT.isDevelopmentLike());
        assertTrue(Environment.TESTING.isDevelopmentLike());
        assertFalse(Environment.PRODUCTION.isDevelopmentLike());

        // Test validation
        assertTrue(Environment.isValidEnvironment("production"));
        assertTrue(Environment.isValidEnvironment("prod"));
        assertFalse(Environment.isValidEnvironment("invalid"));

        // Test case insensitivity
        assertEquals(Environment.PRODUCTION, Environment.fromString("PRODUCTION"));
        assertEquals(Environment.PRODUCTION, Environment.fromString("PROD"));
    }

    @Test
    void testEnumNullHandling() {
        // Test null handling for all enums
        assertThrows(IllegalArgumentException.class, () -> QueueName.fromQueueName(null));
        assertThrows(IllegalArgumentException.class, () -> ExchangeName.fromExchangeName(null));
        assertThrows(IllegalArgumentException.class, () -> UserLogType.fromCode(null));
        assertThrows(IllegalArgumentException.class, () -> LogLevel.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> HttpMethod.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> Environment.fromString(null));

        // Test validation with null
        assertFalse(QueueName.isValidQueueName(null));
        assertFalse(ExchangeName.isValidExchangeName(null));
        assertFalse(UserLogType.isValidCode(null));
        assertFalse(HttpMethod.isValidMethod(null));
        assertFalse(Environment.isValidEnvironment(null));
    }

    @Test
    void testEnumToString() {
        // Test toString implementations
        assertEquals("q.logs-user", QueueName.USER_LOGS.toString());
        assertEquals("x.logging", ExchangeName.LOGGING.toString());
        assertEquals("LOGIN", UserLogType.LOGIN.toString());
        assertEquals("INFO", LogLevel.INFO.toString());
        assertEquals("GET", HttpMethod.GET.toString());
        assertEquals("200 OK", HttpStatus.OK.toString());
        assertEquals("production", Environment.PRODUCTION.toString());
    }

    @Test
    void testORLCommonWithEnums() {
        // Test that ORLCommon constants match enum values
        assertEquals(QueueName.USER_LOGS.getQueueName(), com.openrangelabs.middleware.ORLCommon.USER_LOGS_QUEUE);
        assertEquals(QueueName.USER_LOGS_DLQ.getQueueName(), com.openrangelabs.middleware.ORLCommon.USER_LOGS_DLQ_QUEUE);
        assertEquals(ExchangeName.LOGGING.getExchangeName(), com.openrangelabs.middleware.ORLCommon.LOGGING_EXCHANGE);
        assertEquals(ExchangeName.LOGGING_DLX.getExchangeName(), com.openrangelabs.middleware.ORLCommon.LOGGING_DLX_EXCHANGE);

        // Test helper methods
        assertEquals(QueueName.USER_LOGS, com.openrangelabs.middleware.ORLCommon.getQueue("q.logs-user"));
        assertEquals(ExchangeName.LOGGING, com.openrangelabs.middleware.ORLCommon.getExchange("x.logging"));

        // Test DLQ/DLX detection
        assertTrue(com.openrangelabs.middleware.ORLCommon.isDeadLetterQueue("q.logs-user-dlq"));
        assertFalse(com.openrangelabs.middleware.ORLCommon.isDeadLetterQueue("q.logs-user"));
        assertTrue(com.openrangelabs.middleware.ORLCommon.isDeadLetterExchange("x.logging-dlx"));
        assertFalse(com.openrangelabs.middleware.ORLCommon.isDeadLetterExchange("x.logging"));
    }
}