package io.ola.rbac.query;

import io.ola.crud.query.annotation.Like;
import lombok.Data;

/**
 * @author yiuman
 * @date 2023/9/4
 */
@Data
public class MenuQuery {
    @Like
    private String name;
}
