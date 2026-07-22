package arda.xmlproject.demo.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SecretKey key;
    private final long expirationTime;

    /**
     application.properties içindeki değerleri alıyor
     **/
    public AuthController(@Value("${security.jwt.secret-key}") String secret, @Value("${security.jwt.expiration-time}") long expirationTime) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    // TODO: databasede kullanıcı ve rol kontrolü yap
    // TODO: dto kullanmayı da unutma. veya direkt buna bir dto yaz

    /**
     Postman'de aşağıdaki body'ye sahip bir
     POST isteği atınca token döndürülüyor
     {
     "username": "user",
     "password": "1234"
     }

     Sonra ise bu tokeni yeni GET isteklerimizde
     kullanmamız gerekiyor.
     Authorization -> Auth Type = Baerer Token
     */

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        String role;
        if ("admin".equals(username) && "1234".equals(password)) {
            role = "admin";
        } else if ("user".equals(username) && "1234".equals(password)) {
            role = "user";
        } else {
            throw new RuntimeException("Invalid credentials");
        }

        String token = Jwts.builder()
                .header()
                .keyId("xml-project")
                .and()

                .subject(username) // Tokenin sahibinin username'i
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, Jwts.SIG.HS384)
                .compact();

        return Map.of("token", token, "role", role);
    }
}