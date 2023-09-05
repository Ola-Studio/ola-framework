package io.ola.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证信息
 *
 * @author yiuman
 * @date 2023/8/8
 */
public interface Authentication {

    /**
     * 被认证主体的身份
     */
    Object getPrincipal();

    /**
     * 认证的额外细节
     */
    Object getDetails();

    /**
     * 证明委托人正确的凭据。这通常是一个密码
     */
    Object getCredentials();

    String getGrantType();

    void setGrantType(String grantType);

    boolean isAuthenticated();

    void setAuthenticated(boolean authenticated);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Default implements Authentication {
        private Object principal;
        private Object details;
        private Object credentials;
        private String grantType;
        private boolean authenticated = false;

        public Default(Object principal, Object credentials) {
            this.principal = principal;
            this.credentials = credentials;
            this.authenticated = true;
        }

        public Default(Object principal) {
            this.principal = principal;
        }

        public Default(Object principal, boolean authenticated) {
            this.principal = principal;
            this.authenticated = authenticated;
        }
    }

    /**
     * 匿名用户
     */
    Authentication.Default ANONYMOUS = new Default("ANONYMOUS");
}