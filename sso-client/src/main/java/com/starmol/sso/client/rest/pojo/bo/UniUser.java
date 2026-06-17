//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.starmol.sso.client.rest.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class UniUser implements Serializable {
	private String id;
	private String departmentId;
	private String departmentName;
	private String loginName;
	private String name;
	private Integer type;
	private String roleId;
	private List<SimpleGroupTree> groups;
	private String token;
}
