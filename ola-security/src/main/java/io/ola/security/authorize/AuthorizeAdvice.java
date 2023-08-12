package io.ola.security.authorize;


import cn.hutool.core.lang.Assert;
import io.ola.common.utils.SpringUtils;
import io.ola.security.authenticate.AuthenticateUtils;
import io.ola.security.exception.NoPermissionException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 权限切面,通过权限钩子实现类验证权限
 *
 * @author yiuman
 * @date 2020/4/4
 */
@Aspect
@Component
@EnableAspectJAutoProxy(exposeProxy = true)
public class AuthorizeAdvice {

    /**
     * 声明拦截
     * 1.方法上有使用@Authorize
     * 2.类上有使用@Authorize
     */
    @Pointcut("@annotation(io.ola.security.authorize.Authorize) "
            + "|| @within(io.ola.security.authorize.Authorize) ")
    public void authorizePointCut() {
    }

    /**
     * 拦截请求的方法
     */
    @Pointcut
            ("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
                    + "||@annotation(org.springframework.web.bind.annotation.GetMapping)"
                    + "|| @annotation(org.springframework.web.bind.annotation.PostMapping)"
                    + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "
                    + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) )"
            )
    public void requestPointCut() {

    }

    /**
     * 组合拦截：
     * 1.方法上有使用@Authorize
     * 2.类上有使用@Authorize
     * 3.有@RequestMapping的标记的方法
     */
    @Pointcut("authorizePointCut() || requestPointCut())")
    public void combination() {
    }

    @Around("combination()")
    public Object hasPermission(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        Authorize authorize = Optional.ofNullable(method.getAnnotation(Authorize.class))
                .orElse(point.getTarget().getClass().getAnnotation(Authorize.class));
        if (Objects.nonNull(authorize)) {
            Class<? extends AuthorizeHandler> handlerClass = authorize.value();
            AuthorizeHandler authorizeHandler = SpringUtils.getBean(handlerClass, true);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Assert.isTrue(
                    authorizeHandler.hasPermission(AuthenticateUtils.resolve(request), request),
                    NoPermissionException::new
            );
        }
        return point.proceed();
    }

}
