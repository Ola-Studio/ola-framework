package io.ola.security.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author yiuman
 * @date 2023/8/8
 */
@Data
@Builder
public class Token {
   private String token;
   private String refreshToken;
   private Long expires;
}
