package io.ola.crud.query.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.RawQueryCondition;
import io.ola.crud.CRUD;
import io.ola.crud.enums.Clauses;
import io.ola.crud.model.FieldColumnInfo;
import io.ola.crud.model.QueryFieldMeta;
import io.ola.crud.query.ConditionHandler;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yiuman
 * @date 2023/9/5
 */
public class InConditionHandler implements ConditionHandler {

    @Override
    public Consumer<QueryWrapper> handle(QueryFieldMeta queryFieldMeta, Object conditionValue) {

        return queryWrapper -> {
            Clauses clauses = queryFieldMeta.getClauses();
            QueryCondition condition = queryFieldMeta.getQueryColumn().in((Collection<?>) conditionValue);
            FieldColumnInfo fieldColumnInfo = queryFieldMeta.getColumnInfo();
            if (Objects.nonNull(fieldColumnInfo)) {
                Type type = TypeUtil.getType(fieldColumnInfo.getField());
                if (Collection.class.isAssignableFrom(TypeUtil.getClass(type))) {
                    String jdbcType = CRUD.resolveJDBCType(TypeUtil.getTypeArgument(type).getClass());
                    String paramOccupy = IntStream.range(0, ((Collection<?>) conditionValue).size())
                            .mapToObj(item -> "?").collect(Collectors.joining(","));
                    condition = new RawQueryCondition(
                            StrUtil.format("{} && ARRAY[{}]::{}[]", queryFieldMeta.getMapping(), paramOccupy, jdbcType),
                            ((Collection<?>) conditionValue).toArray()
                    );
                }
            }

            if (Clauses.AND == clauses) {
                queryWrapper.and(condition);
            } else {
                queryWrapper.or(condition);
            }
        };
    }
}
