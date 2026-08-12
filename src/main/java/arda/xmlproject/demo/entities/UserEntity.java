package arda.xmlproject.demo.entities;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ssn;

    private String username;
    private String password;

    private String name;
    private String lastName;

    private LocalDateTime accountCreatedAt;
    private LocalDateTime lastAccessAt;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission", length = 50)
    @Enumerated(EnumType.STRING)
    private Set<ApiPermissions> permissions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RefreshTokenEntity> refreshTokens;


    @PrePersist
    protected void onCreate() {
        this.accountCreatedAt = LocalDateTime.now();
        this.lastAccessAt = LocalDateTime.now();
    }
}