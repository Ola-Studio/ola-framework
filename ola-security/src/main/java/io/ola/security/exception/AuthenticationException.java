package io.ola.security.exception;

/**
 * @author yiuman
 * @date 2023/8/8
 */

public class AuthenticationException extends RuntimeException {
    public static final Integer STATUS_CODE = 401;

    public AuthenticationException() {
        super("UNAUTHORIZED");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}