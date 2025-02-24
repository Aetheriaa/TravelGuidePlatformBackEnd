package top.aetheria.travelguideplatform.common.utils;

import top.aetheria.travelguideplatform.common.constant.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成JWT
    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AppConstants.JWT_CLAIM_USER_ID, userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // 解析JWT
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // 验证JWT是否过期
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // 从JWT中获取用户ID
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null && !isTokenExpired(claims)) {
            return Long.parseLong(claims.get(AppConstants.JWT_CLAIM_USER_ID).toString());
        }
        return null;
    }

    // 刷新JWT
    public String refreshToken(String token){
        Claims claims = parseToken(token);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

}
