package io.ola.rbac.service;

/**
 * @author yiuman
 * @date 2023/8/15
 */
public interface PasswordEncoder {

    String encode(String password);
}