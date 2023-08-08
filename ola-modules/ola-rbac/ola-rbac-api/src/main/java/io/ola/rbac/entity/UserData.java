package io.ola.rbac.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseEntity;
import io.ola.rbac.enums.DataOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户某数据的权限
 * 一般用于数据分享，数据协作
 *
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_user_data")
public class UserData extends BaseEntity<String> {

    private String userId;
    /**
     * 数据的编码
     */
    private String dataCode;
    /**
     * 数据ID
     */
    private String dataId;
    /**
     * 来源
     */
    private String source;
    /**
     * 可操作类型 (默认是所有)
     */
    private DataOperation[] operations = DataOperation.values();
}
