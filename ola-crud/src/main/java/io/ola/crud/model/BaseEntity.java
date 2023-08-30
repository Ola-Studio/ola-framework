package io.ola.crud.model;

import com.mybatisflex.annotation.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @param <ID> ID类型
 * @author yiuman
 * @date 2023/7/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity<ID> extends BaseAudit {
    @Id
    private ID id;

}
