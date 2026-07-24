package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.dto.LoginRequestDto;
import arda.xmlproject.demo.entities.ApiPermissions;
import arda.xmlproject.demo.entities.UserEntity;
import arda.xmlproject.demo.entities.UserRoles;
import arda.xmlproject.demo.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SecretKey key;
    private final long expirationTime;
    private final UserService userService;

    /**
     application.properties içindeki değerleri alıyor
     **/
    public AuthController(@Value("${security.jwt.secret-key}") String secret, @Value("${security.jwt.expiration-time}") long expirationTime, UserService userService) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
        this.userService = userService;
    }

    /**
     * Postmande aşağıdaki body'ye sahip bir POST isteği atınca token geliyor:
     * {
     * "username": "abuzer524",
     * "password": "abuzer524"
     * }

     * abuzer524 = admin
     * sabri524 = country/number yetkileri
     * abuzittin524 = yetkisiz user

     * sonra ise bu tokeni GET isteklerinde kullanıyoruz
     */

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequestDto loginRequest) {

        UserEntity user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // TODO hashing
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Hatalı giriş");
        }

        user.setLastAccessAt(LocalDateTime.now());
        userService.save(user);

        List<String> jwtRoles = new ArrayList<>();
        if (user.getRole() == UserRoles.ADMIN) {
            for (ApiPermissions perm : ApiPermissions.values()) {
                /*
                   Admine her yetki yüklenir, ayrıca admin rolü de yükleniyor.
                   Admin rolü dinamik endpointler için kullanılıyor.
                   databasede tam olarak permission gibi çalışmıyor, ama
                   krakend tarafında diğerleri gibi aynı (rol) olarak çalışıyor
                 */
                jwtRoles.add(perm.name().toLowerCase());
            }
            jwtRoles.add("admin");
        } else if (user.getPermissions() != null) {
            for (ApiPermissions perm : user.getPermissions()) {
                jwtRoles.add(perm.name().toLowerCase());
            }
        }

        String token = Jwts.builder()
                .header()
                .keyId("xml-project")
                .and()
                .subject(user.getUsername()) // subject = tokenin sahibi
                .claim("id", user.getId())
                .claim("roles", jwtRoles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, Jwts.SIG.HS384)
                .compact();

        return Map.of("token", token, "roles", jwtRoles);
    }
}