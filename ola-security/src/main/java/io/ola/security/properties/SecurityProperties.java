package io.ola.security.properties;

import cn.hutool.core.collection.CollUtil;
import io.ola.security.constants.SecurityConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/8/8
 */
@ConfigurationProperties(prefix = "ola.security")
@Data
@Component
public class SecurityProperties {

    private String baseEndpoint = "";

    /**
     * 身份认证的端点
     */
    private String authenticateEndpoint = SecurityConstants.AUTHENTICATE_ENDPOINT;

    /**
     * 登出端点
     */
    private String logoutEndpoint = SecurityConstants.LOGOUT_ENDPOINT;

    /**
     * 验证端点前缀
     */
    private String verifyEndpointPrefix = SecurityConstants.VERIFY_ENDPOINT_PREFIX;

    /**
     * 需排除的url
     */
    private List<String> excludedUris = CollUtil.newArrayList();

}
