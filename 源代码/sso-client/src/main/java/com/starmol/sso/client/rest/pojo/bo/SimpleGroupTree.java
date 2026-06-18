//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.starmol.sso.client.rest.pojo.bo;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class SimpleGroupTree implements Serializable {
	private static final long serialVersionUID = 8495418862356391102L;
	private String id;
	private String name;
	private Boolean selected;
	private List<SimpleGroupTree> childList;
}
