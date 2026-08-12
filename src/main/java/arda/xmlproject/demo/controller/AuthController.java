package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.services.JwtService;
import arda.xmlproject.demo.dto.LoginRequest;
import arda.xmlproject.demo.entities.ApiPermissions;
import arda.xmlproject.demo.entities.RefreshTokenEntity;
import arda.xmlproject.demo.entities.UserEntity;
import arda.xmlproject.demo.entities.UserRoles;
import arda.xmlproject.demo.services.RefreshTokenService;
import arda.xmlproject.demo.services.UserService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final long expirationTime;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    /**
     * application.properties içindeki değerleri alıyor
     **/
    public AuthController(@Value("${security.jwt.secret-key}") String secret, @Value("${security.jwt.expiration-time}") long expirationTime, UserService userService
            , RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.expirationTime = expirationTime;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    /**
     * Postmande aşağıdaki body'ye sahip bir POST isteği atınca token geliyor:
     * {
     * "username": "USERNAME_BURAYA",
     * "password": "PASSWORD_BURAYA"
     * }
     * sonra ise bu tokeni HTTP isteklerinde kullanıyoruz
     *
     * Veya direkt  frontend kullanılabilir
     */

    //TODO access token da buraya taşınabilir fakat süresi kısa olduğu için pek sıkıntılı değil

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        UserEntity user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!checkPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Hatalı giriş");
        }

        user.setLastAccessAt(LocalDateTime.now());
        userService.save(user);

        List<String> jwtRoles = extractRoles(user);
        String accessToken = generateToken(user, jwtRoles);

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/auth/refresh-token");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshCookie);

        return Map.of("token", accessToken, "roles", jwtRoles);
    }


    @PostMapping("/refresh-token")
    public Map<String, Object> refresh(HttpServletRequest request, HttpServletResponse response) {

        // Cookie'den refresh token'ı al
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token bulunamadı!");
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.verifyExpiration(refreshTokenEntity);

        UserEntity user = refreshTokenEntity.getUser();
        List<String> jwtRoles = extractRoles(user);
        String newAccessToken = generateToken(user, jwtRoles);

        RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername());
        Cookie newRefreshCookie = new Cookie("refreshToken", newRefreshToken.getToken());
        newRefreshCookie.setHttpOnly(true);
        newRefreshCookie.setSecure(false);
        newRefreshCookie.setPath("/auth/refresh-token");
        newRefreshCookie.setMaxAge(7 * 24 * 60 * 60);
        newRefreshCookie.setAttribute("SameSite", "Lax");
        response.addCookie(newRefreshCookie);

        return Map.of("token", newAccessToken, "roles", jwtRoles);
    }


    private List<String> extractRoles(UserEntity user) {
        List<String> jwtRoles = new ArrayList<>();
        if (user.getRole() == UserRoles.ADMIN) {
            for (ApiPermissions perm : ApiPermissions.values()) {
                jwtRoles.add(perm.name().toLowerCase());
                /*
                   Admine her yetki yüklenir, ayrıca admin rolü de yükleniyor.
                   Admin rolü dinamik endpointler için kullanılıyor.
                   databasede tam olarak permission gibi çalışmıyor, ama
                   krakend tarafında diğerleri gibi aynı (rol) olarak çalışıyor
                 */
            }
            jwtRoles.add("admin");
        } else if (user.getPermissions() != null) {
            for (ApiPermissions perm : user.getPermissions()) {
                jwtRoles.add(perm.name().toLowerCase());
            }
        }
        return jwtRoles;
    }

    private String rateLimitTier(List<String> jwtRoles) {
        if (jwtRoles.contains("admin")) return "admin";
        return "user";
    }

    private String generateToken(UserEntity user, List<String> jwtRoles) {
        return Jwts.builder()
                .header()
                .keyId("xml-project")
                .and()
                .subject(user.getUsername())
                .claim("id", user.getId())
                .claim("roles", jwtRoles)
                .claim("tier", rateLimitTier(jwtRoles))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtService.getKey(), Jwts.SIG.HS384)
                .compact();
    }

    public static String hashPassword(String password) {
        int logRounds = 16;  // Increasing this value makes it more secure, but slower
        String salt = BCrypt.gensalt(logRounds);

        // Hash the password with the salt
        return BCrypt.hashpw(password, salt);
    }

    public static boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }
}
