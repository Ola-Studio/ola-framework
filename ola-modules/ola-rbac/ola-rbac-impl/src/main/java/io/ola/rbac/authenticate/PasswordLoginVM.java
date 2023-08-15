package io.ola.rbac.authenticate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Data
public class PasswordLoginVM {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
