package io.ola.crud.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @param <ID> ID类型
 * @author yiuman
 * @date 2023/7/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseEntity<ID> extends BaseAudit {
    public abstract ID getId();
}
