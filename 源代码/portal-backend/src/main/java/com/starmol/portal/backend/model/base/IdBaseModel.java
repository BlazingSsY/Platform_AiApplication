package com.starmol.portal.backend.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdBaseModel extends BaseModel implements Serializable {
    @Schema(description = "唯一id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
}