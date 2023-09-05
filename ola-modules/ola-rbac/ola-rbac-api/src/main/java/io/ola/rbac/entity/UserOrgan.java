package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.ola.rbac.constants.TableNames;
import lombok.Data;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@Data
@Table(TableNames.USER_ORGAN)
public class UserOrgan {
    @Id
    private String userId;
    @Id
    private String organId;
}
