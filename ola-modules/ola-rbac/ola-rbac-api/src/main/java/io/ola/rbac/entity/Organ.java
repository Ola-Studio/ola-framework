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
@Table("sys_org")
public class Organ extends BaseTreeEntity<Organ, String> {
    private String organName;
    private String remark;
}
