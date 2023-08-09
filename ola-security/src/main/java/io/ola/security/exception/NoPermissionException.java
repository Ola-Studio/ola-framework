package io.ola.security.exception;

/**
 * @author yiuman
 * @date 2023/8/8
 */

public class NoPermissionException extends RuntimeException {

    public NoPermissionException() {
           super("No Permission");
       }

    public NoPermissionException(String message) {
        super(message);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPermissionException(Throwable cause) {
        super(cause);
    }
}