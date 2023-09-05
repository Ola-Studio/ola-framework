package io.ola.security.starter;

import cn.hutool.core.exceptions.ValidateException;
import io.ola.common.http.R;
import io.ola.common.http.ResultStatus;
import io.ola.security.exception.AuthenticationException;
import io.ola.security.exception.NoPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author yiuman
 * @date 2023/8/11
 */
@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {
    @ExceptionHandler(value = AuthenticationException.class)
    public R<?> authenticationException(AuthenticationException authenticationException) {
        log.info("【{}】", authenticationException.getMessage(), authenticationException);
        return R.error(AuthenticationException.STATUS_CODE, authenticationException.getMessage());
    }

    @ExceptionHandler(value = NoPermissionException.class)
    public R<?> noPermissionException(NoPermissionException noPermissionException) {
        log.info("【{}】", noPermissionException.getMessage(), noPermissionException);
        return R.error(NoPermissionException.STATUS_CODE, noPermissionException.getMessage());
    }

    @ExceptionHandler(value = ValidateException.class)
    public R<?> validateException(ValidateException validateException) {
        log.info("【{}】", validateException.getMessage(), validateException);
        return R.error(ResultStatus.BAD_REQUEST.getStatusCode(), validateException.getMessage());
    }
}
