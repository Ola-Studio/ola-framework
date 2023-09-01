package io.ola.crud.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yiuman
 * @date 2023/8/31
 */
@Data
@Builder
public class IDs implements Serializable {
    private List<FieldValue> ids;
}
