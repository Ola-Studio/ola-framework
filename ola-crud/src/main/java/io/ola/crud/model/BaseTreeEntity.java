package io.ola.crud.model;

import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.RelationOneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @param <ENTITY> 实体类型
 * @param <ID>     实体ID类型
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseTreeEntity<ENTITY extends BaseTreeEntity<ENTITY, ID>, ID extends Serializable>
        extends BaseEntity<ID> {
    private ID parentId;
    @RelationOneToMany(selfField = "id", targetField = "parentId")
    private List<ENTITY> children;
    @RelationManyToOne(selfField = "parentId", targetField = "id")
    private ENTITY parent;
}
