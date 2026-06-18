/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */

package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *用于返回包含用户权限的用户对象
 * @author : Yuexiaopeng
 * @date : 2019/12/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户登录VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginVO extends User implements Serializable {
    private static final long serialVersionUID = 558480086846372889L;

    @Schema(description = "用户角色")
    private Role role;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "权限树")
    private List<PowerAliasTreeVO> powerAliasTree;

    /**
     * 脱钩方法
     */
    public User toUser() {
        User user = new User();
        try {
            BeanUtils.copyProperties(this, user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    /**
     * 对象构造方法
     */
    public static UserLoginVO fromUser(User user) {
        UserLoginVO userVO = new UserLoginVO();
        try {
            BeanUtils.copyProperties(user, userVO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userVO;
    }

}
