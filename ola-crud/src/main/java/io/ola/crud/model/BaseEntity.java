package io.ola.crud.model;

import com.mybatisflex.annotation.Id;
import io.ola.crud.annotation.BeforeSave;
import io.ola.crud.annotation.BeforeUpdate;
import io.ola.crud.inject.UserIdInjector;
import io.ola.crud.inject.impl.NowInjector;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yiuman
 * @date 2023/7/25
 */
@Data
public class BaseEntity<ID extends Serializable> {
    @Id
    private ID id;
    @BeforeSave(UserIdInjector.class)
    private String creator;
    @BeforeUpdate(UserIdInjector.class)
    private String lastModifier;
    @BeforeSave(NowInjector.class)
    private Date createTime;
    @BeforeUpdate(NowInjector.class)
    private Date updateTime;
}
