package io.ola.crud.rest;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import io.ola.common.http.R;
import io.ola.common.utils.SpringUtils;
import io.ola.common.utils.WebUtils;
import io.ola.crud.CRUD;
import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.CRUDService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Objects;


/**
 * CRUD基本接口
 *
 * @author yiuman
 * @date 2023/7/25
 */
@SuppressWarnings("unchecked")
public interface BaseRESTAPI<ENTITY> {

    default CRUDService<ENTITY> getService() {
        return CRUD.getService((Class<? extends BaseRESTAPI<ENTITY>>) getClass());
    }

    @GetMapping
    default Page<ENTITY> page(HttpServletRequest request) {
        cn.hutool.db.Page pageRequest = WebUtils.getPageRequest();
        Page<ENTITY> mfPage = Page.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return getService().page(mfPage, buildWrapper(request));
    }

    @GetMapping("/{id}")
    default R<ENTITY> get(@PathVariable Serializable id) {
        return R.ok(getService().get(id));
    }

    @PostMapping
    default R<ENTITY> post(@RequestBody ENTITY entity) {
        return R.ok(getService().save(entity));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    default R<ENTITY> form(ENTITY entity) {
        return R.ok(getService().save(entity));
    }

    @DeleteMapping("/{id}")
    default R<Void> delete(@PathVariable Serializable id) {
        getService().delete(id);
        return R.ok();
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

    /**
     * 当前当前代理
     *
     * @param <CRUD_INSTANCE> CRUD实例
     * @return 代理类
     */
    default <CRUD_INSTANCE extends BaseRESTAPI<ENTITY>> CRUD_INSTANCE getProxy() {
        return (CRUD_INSTANCE) SpringUtils.getSpringProxyOrThis(this);
    }
}