package kdec.apple.cloud.app.common.dto.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import kdec.apple.cloud.app.common.LoginUser;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    // ⚠️ 和生成 token 时保持一致，后续放到 application.yml 里
    private static final String SECRET = "your-secret-key-replace-me";

    /**
     * 从请求 Header 解析当前登录用户
     * Header 格式：Authorization: Bearer <token>
     * 解析失败（无token/过期/伪造）返回 null，Controller 层判断
     */
    public LoginUser parseUser(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return null;
            }
            String token = header.substring(7);
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            LoginUser user = new LoginUser();
            user.setUserId(Long.valueOf(claims.getSubject()));
            user.setRole(claims.get("role", String.class));
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}