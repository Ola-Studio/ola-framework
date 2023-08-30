package io.ola.rbac.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseTreeEntity;
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
    private String resourceName;
    private Integer type;
    private String code;
    private String icon;
    private String uri;

    @Override
    public void setId(String s) {
        super.setId(s);
    }

    @Override
    public void setParentId(String parentId) {
        super.setParentId(parentId);
    }
}
