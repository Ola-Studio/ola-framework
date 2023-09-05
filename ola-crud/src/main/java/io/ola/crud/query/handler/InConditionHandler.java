package io.ola.crud.query.handler;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.enums.Clauses;
import io.ola.crud.model.QueryFieldMeta;
import io.ola.crud.query.ConditionHandler;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author yiuman
 * @date 2023/9/5
 */
public class InConditionHandler implements ConditionHandler {

    @Override
    public Consumer<QueryWrapper> handle(QueryFieldMeta queryFieldMeta, Object conditionValue) {

        return queryWrapper -> {
            Clauses clauses = queryFieldMeta.getClauses();
            if (Clauses.AND == clauses) {
                queryWrapper.and(queryFieldMeta.getQueryColumn().in((Collection<?>) conditionValue));
            } else {
                queryWrapper.or(queryFieldMeta.getQueryColumn().in((Collection<?>) conditionValue));
            }
        };
    }
}
