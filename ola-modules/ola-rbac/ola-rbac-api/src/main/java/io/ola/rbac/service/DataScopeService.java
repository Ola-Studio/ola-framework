package io.ola.rbac.service;

import io.ola.rbac.model.DataScopeModel;

/**
 * @author yiuman
 * @date 2023/8/4
 */
public interface DataScopeService {
    <SCOPE extends DataScopeModel> SCOPE getReadDataScope();

    <SCOPE extends DataScopeModel> SCOPE getWriteDataScope();
}