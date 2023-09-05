package io.ola.rbac.service;

import cn.hutool.core.collection.CollUtil;
import io.ola.crud.model.BaseTreeEntity;
import io.ola.crud.service.CrudService;
import io.ola.rbac.entity.Organ;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yiuman
 * @date 2023/8/14
 */
public interface OrganService extends CrudService<Organ> {

    static List<String> fetchAllSubOrganIds(List<Organ> organs) {
        List<String> organIds = organs.stream().map(Organ::getId).toList();
        for (Organ organ : organs) {
            if (CollUtil.isNotEmpty(organ.getChildren())) {
                organIds.addAll(fetchAllSubOrganIds(organ.getChildren()));
            }
        }
        return organIds;
    }

    static List<String> fetchAllSupOrganIds(List<Organ> organs) {
        List<String> organIds = organs.stream().map(Organ::getId).toList();
        List<Organ> parents = organs.stream().map(BaseTreeEntity::getParent).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(parents)) {
            organIds.addAll(fetchAllSupOrganIds(parents));
        }
        CollUtil.distinct(organIds);
        return organIds;
    }
}