package com.starmol.sso;

import com.starmol.sso.server.exception.OauthApiException;
import com.starmol.sso.server.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * @author huguojun
 */
public class TokenGenerateTest {

    public static Claims parse(String token, String signKey) throws OauthApiException {
        try {
            return Jwts.parser().setSigningKey(signKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException var5) {
            throw new OauthApiException("登录信息已过期，请重新登录");
        } catch (Exception var6) {
            throw new OauthApiException("登录信息无效，请重新登录");
        }
    }

    @Test
    public void generateTokenTest() {
        final HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("userId", "1573598373102908855");
        userInfoMap.put("username", Lists.newArrayList("sys001"));

        final String air_secretKey = JwtTokenUtil.generateToken(userInfoMap, "air_secretKey", 86400);

        final Claims claims = parse(air_secretKey, "air_secretKey");
        final List<String> username = claims.get("username", List.class);
        System.out.println(username);
    }
}
