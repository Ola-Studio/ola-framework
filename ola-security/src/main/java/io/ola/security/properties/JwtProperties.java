package io.ola.security.properties;

import io.jsonwebtoken.SignatureAlgorithm;
import io.ola.security.constants.JwtConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yiuman
 * @date 2023/8/9
 */
@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties(prefix = "ola.security.jwt")
@Data
public class JwtProperties {

    private SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

    /**
     * token请求头
     */
    private String tokenHeader = JwtConstants.TOKEN_HEADER;

    /**
     * token参数名（请求头没有时从请求参数中取）
     */
    private String tokenParamName = JwtConstants.TOKEN_PARAM_NAME;

    /**
     * token值的前缀，如Bearer （注意此处有空格）
     */
    private String tokenPrefix = JwtConstants.TOKEN_PREFIX;

    /**
     * JWT签名
     * 必须使用最少88位的Base64对该令牌进行编码
     */
    private String secret = JwtConstants.SECRET;

    /**
     * 身份认证的标识值，用于后面根据标识值解析出当前认证用户实例
     */
    private String identityKey = JwtConstants.IDENTITY_KEY;

    /**
     * 过期时间
     */
    private long expiresInSeconds = JwtConstants.EXPIRES_IN_SECOND;

    /**
     * 记住我的时间
     */
    private long rememberMeInSeconds = JwtConstants.REMEMBER_ME_IN_SECONDS;
}
