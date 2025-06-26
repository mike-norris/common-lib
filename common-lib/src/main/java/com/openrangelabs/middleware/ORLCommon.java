package com.openrangelabs.middleware;

public class ORLCommon {

    public static final String LOGGING_EXCHANGE = "x.logging";
    public static final String LOGGING_DLX_EXCHANGE = "x.logging-dlx";
    public static final String USER_LOGS_QUEUE = "q.logs-user";
    public static final String USER_LOGS_DLQ_QUEUE = "q.logs-user-dlq";
    public static final String SYSTEM_LOGS_QUEUE = "q.logs-system";
    public static final String SYSTEM_LOGS_DLQ_QUEUE = "q.logs-system-dlq";

    public static final String CREATE_USER_DLX_EXCHANGE = "x.create-user-dlx";
    public static final String CREATE_USER_EXCHANGE = "x.create-user";
    public static final String PORTAL_USER_QUEUE = "q.portal-user";
    public static final String PORTAL_USER_DLQ_QUEUE = "q.portal-user-dlq";

    public ORLCommon() {
    }

}