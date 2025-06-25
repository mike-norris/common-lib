package com.mydcblox.middleware.logging;

public class DCBException extends Exception {

    public DCBException(String message) { super(message); }

    public DCBException(String message, Exception e) { super(message, e); }
}
