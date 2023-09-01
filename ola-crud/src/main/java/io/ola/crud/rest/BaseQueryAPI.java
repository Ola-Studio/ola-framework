package io.ola.crud.rest;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.common.http.R;
import io.ola.common.utils.WebUtils;
import io.ola.crud.CRUD;
import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.QueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/8/31
 */
@SuppressWarnings("unchecked")
public interface BaseQueryAPI<ENTITY> {

    default QueryService<ENTITY> getQueryService() {
        return CRUD.getQueryService(getClass());
    }

    @GetMapping
    default Page<ENTITY> page(HttpServletRequest request) {
        cn.hutool.db.Page pageRequest = WebUtils.getPageRequest();
        Page<ENTITY> mfPage = Page.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return getQueryService().page(mfPage, buildWrapper(request));
    }

    @GetMapping("/{id}")
    default R<ENTITY> get(@PathVariable Serializable id) {
        return R.ok(getQueryService().get(id));
    }

    /**
     * 构建查询
     *
     * @param request http请求
     * @return 查询包装器
     */
    default QueryWrapper buildWrapper(HttpServletRequest request) {
        Class<?> queryClass = CRUD.getQueryClass((Class<? extends BaseRESTAPI<ENTITY>>) getClass());
        if (Objects.isNull(queryClass)) {
            return QueryWrapper.create();
        }
        Object queryObject = WebUtils.requestDataBind(queryClass, request);
        return QueryHelper.build(queryObject);
    }
}