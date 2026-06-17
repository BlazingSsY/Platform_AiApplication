
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
 *用于返回包含部门名称的用户列表
 * @author : Yuexiaopeng
 * @date : 2019/12/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO extends User implements Serializable {
    private static final long serialVersionUID = 558480086846371889L;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "默认角色ID")
    private Long defaultRoleId;

    @Schema(description = "默认角色名称")
    private String defaultRoleName;

    @Schema(description = "角色名称")
    private List<Role> roleId;

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
    public static UserVO fromUser(User user) {
        UserVO userVO = new UserVO();
        try {
            BeanUtils.copyProperties(user, userVO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userVO;
    }

}
