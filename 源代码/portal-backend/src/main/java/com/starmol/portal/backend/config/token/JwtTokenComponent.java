package com.starmol.portal.backend.config.token;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.common.UniUserDTO;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.utils.IdWorker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtTokenComponent {
    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_COOKIE_KEY = "token";

    public static final String REFRESH_COOKIE_KEY = "refresh";

    public static final String AUTH_PREFIX = "Bearer-";

    /**
     * Token打包的内容
     */
    public static final String ID = "id";
    public static final String DEPARTMENT_ID = "departmentId";
    public static final String TENANT_ID = "tenantId";
    public static final String DEPARTMENT_NAME = "departmentName";
    public static final String LOGIN_NAME = "loginName";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String GROUPS = "groups";

    public static final String TOKEN_REFRESH_KEY = "CpSS5hHUEeuYWQJCwKhAAw";

    public static final String TOKEN_INVALID = "登录信息无效，请重新登录";
    public static final String REFRESH_TOKEN_EXPIRED = "登录信息已过期，请重新登录";
    /**
     * this one cannot be changed unless changing UI accordingly.
     */
    private static final String TOKEN_EXPIRED = "登录信息已过期，请重新登录";
    private final SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

    private static final Integer SYSTEM_USER_TYPE = 4;

    @Resource
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenSettings settings;

    public String generateToken(@NotNull Map<String, Object> userData) {
        LocalDateTime currentTime = LocalDateTime.now();
        final Object id = userData.get(ID);
        String tokenId = Objects.nonNull(id) ? String.valueOf(id).concat(IdWorker.getInstance().nextUUID()) : IdWorker.getInstance().nextUUID();
        final JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(String.valueOf(userData.get(LOGIN_NAME)))
                .setId(tokenId);
        for (Map.Entry<String, Object> entry : userData.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            jwtBuilder.claim(key, value);
        }

        final String token = jwtBuilder.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime.plusMinutes(settings.getTokenExpirationTime()).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(algorithm, settings.getTokenSigningKey())
                .compact();

        return AUTH_PREFIX + token;
    }

    public String generateRefreshToken(String loginName, String name) {
        LocalDateTime currentTime = LocalDateTime.now();
        final String compact = Jwts.builder()
                .setId(TOKEN_REFRESH_KEY)
                .claim(LOGIN_NAME, loginName)
                .claim(NAME, name)
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusHours(settings.getRefreshTokenExpTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(algorithm, settings.getTokenSigningKey())
                .compact();
        return AUTH_PREFIX + compact;
    }


    public Claims parse(String token) {
        Claims claims = getClaims(token, false);
        // token must include subject.
        if (claims != null && claims.getSubject() != null) {
            return claims;
        } else {
            throw new KnowException(TOKEN_INVALID);
        }
    }

    public String parseRefreshToken(String token) {
        Claims claims = getClaims(token, true);
        // refresh token must include an id.
        if (claims != null && claims.getId() != null && claims.getId().equals(TOKEN_REFRESH_KEY)) {
            return claims.get(LOGIN_NAME, String.class);
        } else {
            throw new KnowException(TOKEN_INVALID);
        }
    }

    public UniUserDTO parseUser(String token) {
        Claims claims = this.parse(token);
        return createClaims(claims, UniUserDTO.class);
    }

    private Claims getClaims(String token, boolean isRefresh) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(settings.getTokenSigningKey()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException eje) {
            if (isRefresh) {
                throw new KnowException(REFRESH_TOKEN_EXPIRED);
            } else {
                throw new KnowException(TOKEN_EXPIRED);
            }
        } catch (Exception e) {
            throw new KnowException(TOKEN_INVALID);
        }
        return claims;
    }

    @SneakyThrows
    protected <T> T createClaims(Claims claims, Class<T> clazz) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(claims, clazz);
    }

}
