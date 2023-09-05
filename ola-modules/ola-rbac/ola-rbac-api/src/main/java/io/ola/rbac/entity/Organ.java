package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.ola.crud.model.BaseTreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_org")
public class Organ extends BaseTreeEntity<Organ, String> {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    private String name;
    private String remark;
}
