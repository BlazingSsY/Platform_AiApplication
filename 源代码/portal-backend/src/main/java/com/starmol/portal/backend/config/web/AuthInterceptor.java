package com.starmol.portal.backend.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.common.UniUserDTO;
import com.starmol.portal.backend.config.token.JwtTokenComponent;
import com.starmol.portal.backend.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.security.auth.message.AuthException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements UniAuthInterceptor {
    private static final String SWAGGER_RESOURCES_PATH = "/swagger-resources";
    private static final String API_DOCS_PATH = "/api-docs";
    private static final String API_DOCS_EXT_PATH = "/api-docs-ext";
    private static final String SWAGGER_UI = "swagger-ui";

    @Autowired
    private JwtTokenComponent jwtTokenComponent;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        final String path = request.getServletPath();
        if (checkURL(path)) {
            return true;
        }

        Permit permit = authPermit(handler, path);
        // Skip token validation when Permit is All.
        if (permit != null && Permission.ALL.equals(permit.value())) {
            return true;
        }
        UniUserDTO uniUser;
        try {
            final String token = getTokenFromRequest(request);
            uniUser = authToken(token);
            HttpRequestUtil.setUser(uniUser);
            return true;
        } catch (AuthException authE) {
            response.sendError(401, authE.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Invalid token was found");
            response.sendError(401, e.getMessage());
            throw e;
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final Cookie cookie = Optional.ofNullable(WebUtils.getCookie(request, JwtTokenComponent.TOKEN_COOKIE_KEY)).orElse(new Cookie(JwtTokenComponent.TOKEN_COOKIE_KEY, null));
        String token = cookie.getValue();
        if (StringUtils.isBlank(token)) {
            token = request.getHeader(JwtTokenComponent.AUTHORIZATION);
        }
        return token;
    }

    private boolean checkURL(String path) {
        if (path != null) {
            path = path.toLowerCase();
        }
        return (path != null) && ((path.contains(SWAGGER_RESOURCES_PATH) || path.contains(API_DOCS_PATH) ||
                path.contains(API_DOCS_EXT_PATH) || path.contains(SWAGGER_UI) ||
                path.equals("/") ||
                path.equals("/error") ||
                path.equals("/csrf") ||
                path.contains(".css") ||
                path.contains(".js") ||
                path.contains(".jpg") ||
                path.contains(".png") ||
                path.contains(".gif") ||
                path.contains(".bmp") ||
                path.contains(".jpeg") ||
                path.contains(".zip") ||
                path.contains(".xls") ||
                path.contains(".woff") ||
                path.contains("/favicon.ico") ||
                path.contains("doc.html") ||
                path.contains("/index.html")));
    }

    private Permit authPermit(Object handler, String path) {
        Method method;
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod hm = (HandlerMethod) handler;
                method = hm.getMethod();
            } else {
                return null;
            }
        } catch (Exception var7) {
            log.error(String.format("Cast handler for %s with error: " + var7.getMessage(), path), var7);
            return null;
        }

        Class<?> declaringClass = method.getDeclaringClass();

        Permit permit = null;
        if (method.isAnnotationPresent(Permit.class)) {
            permit = method.getAnnotation(Permit.class);
        } else if (declaringClass.isAnnotationPresent(Permit.class)) {
            permit = declaringClass.getAnnotation(Permit.class);
        }

        return permit;
    }

    private UniUserDTO authToken(String authHeader) throws AuthException {
        if (authHeader == null || !authHeader.startsWith(JwtTokenComponent.AUTH_PREFIX)) {
            throw new AuthException(JwtTokenComponent.TOKEN_INVALID);
        }
        final String token = authHeader.substring(JwtTokenComponent.AUTH_PREFIX.length());
        final UniUserDTO userDTO = jwtTokenComponent.parseUser(token);
        userDTO.setToken(token);
        return userDTO;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


}