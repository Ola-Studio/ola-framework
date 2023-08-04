package io.ola.rbac.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseTreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_resource")
public class Resource extends BaseTreeEntity<Resource, String> {
    private String resourceName;
    private Integer type;
    private String code;
    private String icon;
    private String uri;
}
