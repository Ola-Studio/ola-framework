package io.ola.rbac.service;

import io.ola.rbac.entity.ResourceOwner;

import java.util.List;

/**
 * @author yiuman
 * @date 2024/5/6
 */
public interface ResourceOwnerService {

    List<ResourceOwner> findListByUserId(String userId);
}