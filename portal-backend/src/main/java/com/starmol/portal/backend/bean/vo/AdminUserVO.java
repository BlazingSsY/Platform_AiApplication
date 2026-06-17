package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.lang.NonNull;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(description = "管理员VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Accessors(chain = true)
public class AdminUserVO implements Serializable {
    private static final long serialVersionUID = 558480086846371889L;

    private Long id;

    /**
     * 登录名，不可更改
     */
    @NonNull
    private String name;
    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String telephone;

    private Boolean isDeleted;

}
