package io.ola.crud.model;

import io.ola.crud.annotation.BeforeSave;
import io.ola.crud.annotation.BeforeUpdate;
import io.ola.crud.inject.UserIdInjector;
import io.ola.crud.inject.impl.NowInjector;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Data
public class BaseAudit {
    @BeforeSave(UserIdInjector.class)
    private String creator;
    @BeforeUpdate(UserIdInjector.class)
    private String lastModifier;
    @BeforeSave(NowInjector.class)
    private LocalDateTime createTime;
    @BeforeSave(NowInjector.class)
    @BeforeUpdate(NowInjector.class)
    private LocalDateTime updateTime;
}
