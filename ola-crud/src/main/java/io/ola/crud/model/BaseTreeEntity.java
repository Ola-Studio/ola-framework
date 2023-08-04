package io.ola.crud.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
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
