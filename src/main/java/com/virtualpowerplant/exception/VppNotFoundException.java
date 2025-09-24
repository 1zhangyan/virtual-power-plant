package com.virtualpowerplant.exception;

/**
 * 虚拟电厂未找到异常
 */
public class VppNotFoundException extends RuntimeException {

    public VppNotFoundException(String message) {
        super(message);
    }

    public VppNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}