package io.ola.security.constants;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.IdUtil;

/**
 * @author yiuman
 * @date 2023/8/9
 */
public interface JwtConstants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PARAM_NAME = TOKEN_HEADER;
    String TOKEN_PREFIX = "Bearer ";
    String SECRET = Base64Encoder.encode(IdUtil.fastUUID() + IdUtil.fastUUID());
    String IDENTITY_KEY = "Identity";
    Long EXPIRES_IN_SECOND = 900L;
    Long REMEMBER_ME_IN_SECONDS = 10080L;
}