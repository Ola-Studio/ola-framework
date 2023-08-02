package io.ola.crud.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 删除标记，通用CRUD调用delete的时候 遇到会调用update
 *
 * @author yiuman
 * @date 2021/8/19
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteTag {
}