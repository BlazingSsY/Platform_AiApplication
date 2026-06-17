package com.starmol.circuitreview.backend.utils;

import com.starmol.circuitreview.backend.common.UniUserDTO;
import com.starmol.circuitreview.backend.common.UserMetaData;
import com.starmol.circuitreview.backend.constant.SysRoleTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class HttpRequestUtil {

	private static final ThreadLocal<UserMetaData> USER_HOLDER = new ThreadLocal<>();
	private static final ThreadLocal<String>  DEBUG_ID_HOLDER = new ThreadLocal<>();


	public static HttpServletResponse getResponse() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			return requestAttributes.getResponse();
		}
		throw new KnowException("get request object failed");
	}


	public static HttpServletRequest getRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			return requestAttributes.getRequest();
		}
		throw new KnowException("get request object failed");
	}

	public static UserMetaData getUser() {
		return USER_HOLDER.get();
	}

	public static Long getUserId() {
		UserMetaData userMetaData = USER_HOLDER.get();
		if (Objects.nonNull(userMetaData)) {
			return userMetaData.getId();
		} else {
			return 0L;
		}
	}

	public static String getLoginName() {
		UserMetaData userMetaData = USER_HOLDER.get();
		if (Objects.nonNull(userMetaData)) {
			return userMetaData.getLoginName();
		} else {
			return "system";
		}
	}

	public static Long getDepartmentId() {
		UserMetaData userMetaData = USER_HOLDER.get();
		if (Objects.nonNull(userMetaData)) {
			return userMetaData.getDepartmentId();
		} else {
			return 0L;
		}
	}

	public static SysRoleTypeEnum getSysRoleType() {
		UserMetaData userMetaData = USER_HOLDER.get();
		if (Objects.nonNull(userMetaData)) {
			return userMetaData.getSysRoleType();
		} else {
			return SysRoleTypeEnum.NORMAL_USER;
		}
	}

	public static void setUser(UserMetaData uniUser) {
		USER_HOLDER.set(uniUser);
		DEBUG_ID_HOLDER.set(UUIDUtils.generateRandomUUID().toLowerCase());
	}

	public static String getDebugId() {
		String debugId = DEBUG_ID_HOLDER.get();
		if (Objects.nonNull(debugId)) {
			return debugId;
		} else {
			//默认值(定时任务调用时使用)
			return "11111111-1111-1111-1111-111111111111";
		}
	}
}
