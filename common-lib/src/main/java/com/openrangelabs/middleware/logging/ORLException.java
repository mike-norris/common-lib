package com.openrangelabs.middleware.logging;

public class ORLException extends Exception {

    public ORLException(String message) { super(message); }

    public ORLException(String message, Exception e) { super(message, e); }
}
