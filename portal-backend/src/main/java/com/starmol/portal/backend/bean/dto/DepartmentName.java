package com.starmol.portal.backend.bean.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DepartmentName {

    private String name;

    private String fullName;

}