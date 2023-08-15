package io.ola.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yiuman
 * @date 2023/8/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenVM {
    public static final String GRANT_TYPE = "refreshToken";
    private String refreshToken;
}
