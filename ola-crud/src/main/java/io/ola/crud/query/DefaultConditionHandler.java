package io.ola.crud.query;

import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.enums.Clauses;
import io.ola.crud.model.QueryFieldMeta;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author yiuman
 * @date 2023/7/31
 */
public enum DefaultConditionHandler implements ConditionHandler {
    INSTANCE;

    private static final Consumer<QueryWrapper> UNDO = queryWrapper -> {
    };

    @Override
    public Consumer<QueryWrapper> handle(QueryFieldMeta queryFieldMeta, Object conditionValue) {
        if (Objects.isNull(conditionValue)) {
            return UNDO;
        }
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setColumn(queryFieldMeta.getQueryColumn());
        queryCondition.setLogic(queryFieldMeta.getMethod());
        queryCondition.setValue(conditionValue);
        return queryWrapper -> {
            if (Clauses.AND == queryFieldMeta.getClauses()) {
                queryWrapper.and(queryCondition);
            } else {
                queryWrapper.or(queryCondition);
            }
        };
    }

}
