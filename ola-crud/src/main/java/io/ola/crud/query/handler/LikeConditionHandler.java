package io.ola.crud.query.handler;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.model.QueryFieldMeta;
import io.ola.crud.query.ConditionHandler;
import io.ola.crud.query.DefaultConditionHandler;

import java.util.function.Consumer;

/**
 * @author yiuman
 * @date 2023/9/1
 */
public class LikeConditionHandler implements ConditionHandler {

    @Override
    public Consumer<QueryWrapper> handle(QueryFieldMeta queryFieldMeta, Object conditionValue) {
        return DefaultConditionHandler.INSTANCE.handle(queryFieldMeta, StrUtil.format("%{}%", conditionValue));
    }
}
