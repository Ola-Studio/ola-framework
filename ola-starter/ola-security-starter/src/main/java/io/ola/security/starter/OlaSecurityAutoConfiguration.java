package io.ola.security.starter;

import io.ola.common.constants.OLA;
import io.ola.security.authenticate.AuthenticateAPI;
import io.ola.security.authenticate.RefreshTokenAuthenticateServiceImpl;
import io.ola.security.authenticate.TokenService;
import io.ola.security.authorize.AllowAll;
import io.ola.security.authorize.Authorize;
import io.ola.security.authorize.AuthorizeHandlerInterceptor;
import io.ola.security.authorize.RequestAuthorizeHandler;
import io.ola.security.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/8/11
 */
@Configuration
@ComponentScan(OLA.BASE_PACKAGE)
@EnableConfigurationProperties(SecurityProperties.class)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class OlaSecurityAutoConfiguration {
    private static final String ALL = "*";
    private static final String ALL_URIS = "/**";

    @RestController
    @RequestMapping("#{securityProperties.baseEndpoint}")
    @Authorize(AllowAll.class)
    public static class AuthenticateController implements AuthenticateAPI {
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizeHandlerInterceptor.class)
    public AuthorizeHandlerInterceptor authorizeHandlerInterceptor(SecurityProperties securityProperties, List<RequestAuthorizeHandler> requestAuthorizeHandlers) {
        return new AuthorizeHandlerInterceptor(securityProperties, requestAuthorizeHandlers);
    }

    @Bean
    @ConditionalOnMissingBean(RequestAuthorizeHandler.class)
    public RequestAuthorizeHandler requestAuthorizeHandler() {
        return new RequestAuthorizeHandler.Default();
    }

    @Configuration
    @ConditionalOnBean(AuthorizeHandlerInterceptor.class)
    @RequiredArgsConstructor
    public static class WebMvcConfiguration implements WebMvcConfigurer {

        private final AuthorizeHandlerInterceptor authorizeHandlerInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(authorizeHandlerInterceptor).addPathPatterns(ALL_URIS);
        }
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService() {
        return new TokenService() {
        };
    }

    @Bean
    @ConditionalOnMissingBean(RefreshTokenAuthenticateServiceImpl.class)
    public RefreshTokenAuthenticateServiceImpl refreshTokenAuthenticateService(TokenService tokenService) {
        return new RefreshTokenAuthenticateServiceImpl(tokenService);
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许跨域请求的域名
        config.addAllowedOrigin(ALL);
        // 是否允许证书 不再默认开启
        config.setAllowCredentials(true);
        // 设置允许的方法
        config.addAllowedMethod(ALL);
        // 允许任何头
        config.addAllowedHeader(ALL);
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(ALL_URIS, config);
        return new CorsFilter(configSource);
    }

}
