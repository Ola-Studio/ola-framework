package io.ola.rbac.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import io.ola.rbac.service.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Component
public class PasswordEncoderImpl implements PasswordEncoder {

    @Override
    public String encode(String password) {
        return DigestUtil.bcrypt(password);
    }
}
