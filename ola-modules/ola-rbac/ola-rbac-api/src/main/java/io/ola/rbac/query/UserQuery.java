package io.ola.rbac.query;

import io.ola.crud.query.annotation.Like;
import lombok.Data;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Data
public class UserQuery {
    @Like
    private String username;
}
