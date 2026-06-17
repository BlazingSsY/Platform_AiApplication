/*
 *  YUNLIAN Confidential
 * OCO Source Materials
 * YUNLIAN
 * © Copyright YunLian Advanced Technology Ltd. 2019-2020
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the P.R.China Copyright Office
 */

package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class RolePowerInfo implements Serializable {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("power_info_list")
    private List<RolePowerDto> powerInfoList;
}