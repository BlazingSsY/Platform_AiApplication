package com.starmol.portal.backend.common;

import com.starmol.portal.backend.constant.SysRoleTypeEnum;

public interface UserMetaData {
    default long getTenantId() {
        return 0L;
    }

    default long getDepartmentId() {return 0L;}

    default String getLoginName() {
        return null;
    }
    default String getName() {
        return null;
    }

    default long getId() {
        return 0L;
    }

    default SysRoleTypeEnum getSysRoleType() {
        return SysRoleTypeEnum.NORMAL_USER;
    }
}
