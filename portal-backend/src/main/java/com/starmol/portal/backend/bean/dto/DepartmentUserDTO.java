package com.starmol.portal.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentUserDTO implements Serializable {
    private Map<Long, Long> staffCountWithAll;
    private Map<Long, Long> staffCountWithDirect;
}
