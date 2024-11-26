package io.ola.crud.rest;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.common.http.R;
import io.ola.common.utils.WebUtils;
import io.ola.crud.CRUD;
import io.ola.crud.inject.ConditionInjector;
import io.ola.crud.model.CrudMeta;
import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.QueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/31
 */
@SuppressWarnings("unchecked")
public interface BaseQueryAPI<ENTITY> {

    default QueryService<ENTITY> getQueryService() {
        return CRUD.getQueryService(getClass());
    }

    @GetMapping
    default <T extends ENTITY> R<Page<T>> page(HttpServletRequest request) {
        cn.hutool.db.Page pageRequest = WebUtils.getPageRequest();
        Page<ENTITY> mfPage = Page.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return R.ok(getQueryService().page(mfPage, buildWrapper(request)));
    }

    @GetMapping("/list")
    default <T extends ENTITY> R<List<T>> list(HttpServletRequest request) {
        return R.ok(getQueryService().list(buildWrapper(request)));
    }

    @GetMapping("/{id}")
    default R<ENTITY> get(@PathVariable("id") Serializable id) {
        return R.ok(getQueryService().get(id));
    }

    /**
     * 构建查询
     *
     * @param request http请求
     * @return 查询包装器
     */
    default QueryWrapper buildWrapper(HttpServletRequest request) {
        CrudMeta<?> crudMeta = CRUD.getCRUDMeta(getClass());
        Class<?> queryClass = crudMeta.getQueryClass();
        QueryWrapper queryWrapper;
        if (Objects.isNull(queryClass)) {
            queryWrapper = QueryWrapper.create();
        } else {
            Object queryObject = getQueryObject(request, queryClass);
            queryWrapper = QueryHelper.build(queryObject, crudMeta.getEntityClass());
        }

        ConditionInjector conditionInjector = crudMeta.getConditionInjector();
        if (Objects.nonNull(conditionInjector)) {
            conditionInjector.inject(queryWrapper, crudMeta.getEntityClass());
        }
        return queryWrapper;
    }

    default Object getQueryObject(HttpServletRequest request, Class<?> queryClass) {
        return WebUtils.requestDataBind(queryClass, request);
    }
}