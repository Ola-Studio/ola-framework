package io.ola.rbac.query;

import io.ola.crud.query.annotation.Equals;
import io.ola.crud.query.annotation.In;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@Data
@Builder
public class ResourceQuery {
    @Equals
    private String uri;
    @In(mapping = "id")
    private Set<String> ids;
    private String userId;
}
