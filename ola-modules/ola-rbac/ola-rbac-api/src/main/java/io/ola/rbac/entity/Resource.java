package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.ola.crud.model.BaseTreeEntity;
import io.ola.rbac.enums.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_resource")
@Setter
public class Resource extends BaseTreeEntity<Resource, String> {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    private String name;
    private ResourceType type;
    private String code;
    private String icon;
    private String uri;
    private String method;
    private String expression;
    private String remark;
}
