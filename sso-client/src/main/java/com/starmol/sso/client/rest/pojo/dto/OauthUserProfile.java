package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class OauthUserProfile {

    private static final long serialVersionUID = 8098354063458373513L;

    private String id;
    private String loginName;
    private String userPhone;
    private String departmentId;
    private String departmentName;
    private String name;
    private String roleId;
    private String roleName;
    private List<PowerAliasTreeVO> powerAliasTree;

    private Long iat;
    private Long exp;

    private String profile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("loginName")
    public String getLoginName() {
        return loginName;
    }

    @JsonProperty("login_name")
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @JsonProperty("userPhone")
    public String getUserPhone() {
        return userPhone;
    }

    @JsonProperty("user_phone")
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @JsonProperty("departmentId")
    public String getDepartmentId() {
        return departmentId;
    }

    @JsonProperty("department_id")
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @JsonProperty("departmentName")
    public String getDepartmentName() {
        return departmentName;
    }

    @JsonProperty("department_name")
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("roleId")
    public String getRoleId() {
        return roleId;
    }

    @JsonProperty("role_id")
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @JsonProperty("roleName")
    public String getRoleName() {
        return roleName;
    }

    @JsonProperty("role_name")
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonProperty("powerAliasTree")
    public List<PowerAliasTreeVO> getPowerAliasTree() {
        return powerAliasTree;
    }

    @JsonProperty("power_alias_tree")
    public void setPowerAliasTree(List<PowerAliasTreeVO> powerAliasTree) {
        this.powerAliasTree = powerAliasTree;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
