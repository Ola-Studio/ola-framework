package io.ola.rbac.query;

import io.ola.crud.query.annotation.Equals;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataQuery {
    @Equals
    private String userId;
    @Equals
    private String code;
}
