package io.ola.crud.rest;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.validation.BeanValidationResult;
import cn.hutool.extra.validation.ValidationUtil;
import io.ola.common.http.R;
import io.ola.common.http.ResultStatus;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.CRUD;
import io.ola.crud.service.CrudService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;


/**
 * CRUD基本接口
 *
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/7/25
 */
@SuppressWarnings("unchecked")
public interface BaseRESTAPI<ENTITY> extends BaseQueryAPI<ENTITY> {

    default CrudService<ENTITY> getCrudService() {
        return CRUD.getService(getClass());
    }

    @PostMapping
    default <T extends ENTITY> R<T> post(@RequestBody ENTITY entity) {
        return R.ok(getProxy().doValidateAndSave(entity));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    default <T extends ENTITY> R<T> form(ENTITY entity) {
        return R.ok(getProxy().doValidateAndSave(entity));
    }

    @DeleteMapping("/{id}")
    default R<Void> delete(@PathVariable("id") Serializable id) {
        getCrudService().delete(id);
        return R.ok();
    }

    default <T extends ENTITY> T doValidateAndSave(ENTITY entity) {
        CrudService<ENTITY> service = getCrudService();
        Class<?>[] validateGroups = service.isNew(entity) ? CRUD.SAVE_GROUPS : CRUD.MODIFY_GROUPS;
        BeanValidationResult beanValidationResult = ValidationUtil.warpValidate(entity, validateGroups);
        Assert.isTrue(beanValidationResult.isSuccess(), () -> new ValidateException(
                ResultStatus.BAD_REQUEST.getStatusCode(),
                StrUtil.join(",", beanValidationResult.getErrorMessages()
                        .stream()
                        .map(BeanValidationResult.ErrorMessage::getMessage)
                        .toList()
                )
        ));
        return (T) service.save(entity);
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