package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

import com.starmol.portal.backend.model.Power;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用于展示权限别名树结构信息
 *
 * @author Yuexiaopeng
 * @date 2019/9/26
 **/
@Data
@Schema(description = "权限别名树结构信息VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerAliasTreeVO extends Power implements Serializable {
    /**
     * 子节点列表
     */
    @Schema(description = "子节点列表")
    private List<PowerAliasTreeVO> childList;

    /**
     * 脱钩方法
     */
    public Power toPower() {
        Power power = new Power();
        try {
            BeanUtils.copyProperties(this, power);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return power;
    }

    /**
     * 对象构造方法
     */
    public static PowerAliasTreeVO fromPower(Power power) {
        PowerAliasTreeVO powerAliasTreeVO = new PowerAliasTreeVO();
        try {
            BeanUtils.copyProperties(power, powerAliasTreeVO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return powerAliasTreeVO;
    }
}