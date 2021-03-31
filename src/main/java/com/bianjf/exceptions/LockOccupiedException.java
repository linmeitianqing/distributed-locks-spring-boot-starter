package com.bianjf.exceptions;

/**
 * 锁被占用异常
 */
public class LockOccupiedException extends CustomDefaultException {
    private static final long serialVersionUID = 2470627894642499710L;

    public LockOccupiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockOccupiedException(String message) {
        super(message);
    }
}
