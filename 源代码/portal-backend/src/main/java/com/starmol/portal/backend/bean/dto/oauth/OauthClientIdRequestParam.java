package com.starmol.portal.backend.bean.dto.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OauthClientIdRequestParam implements Serializable {

    private static final long serialVersionUID = 4188388640604233317L;

    @NotEmpty(message = "idList 不能为空")
    @Size(min = 1, message = "idList 至少需要一个元素")
    private List<Long> idList;

}
