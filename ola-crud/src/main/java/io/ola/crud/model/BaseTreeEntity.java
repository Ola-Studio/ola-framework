package io.ola.crud.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @param <ENTITY> 实体类型
 * @param <ID> 实体ID类型
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseTreeEntity<ENTITY extends BaseTreeEntity<ENTITY, ID>, ID extends Serializable>
        extends BaseEntity<ID> {
    private ID parentId;
    private List<ENTITY> children;
}
