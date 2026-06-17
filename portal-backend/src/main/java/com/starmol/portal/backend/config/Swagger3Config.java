/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */

package com.starmol.portal.backend.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * open api配置
 *
 * @author yuexiaopeng
 * @version 1.0
 * @date 16:43 2022/11/21
 */

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "基础功能管理REST APIs",
                version = "1.0",
                description = "包含部门、用户、角色及权限的基础功能管理 APIs"
        ),
        security = @SecurityRequirement(
                name = "Authorization",
                scopes = {"global", "accessEverything"}
        )
)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class Swagger3Config {

}