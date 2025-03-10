package io.ola.crud.query;

import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.model.QueryFieldMeta;

import java.util.function.Consumer;

/**
 * @author yiuman
 * @date 2023/7/31
 */
public interface ConditionHandler {

    Consumer<QueryWrapper> handle(QueryFieldMeta queryFieldMeta, Object conditionValue);
}