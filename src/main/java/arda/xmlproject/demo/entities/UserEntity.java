package arda.xmlproject.demo.entities;

import jakarta.persistence.*;
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
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<ApiPermissions> permissions;


    @PrePersist
    protected void onCreate() {
        this.accountCreatedAt = LocalDateTime.now();
        this.lastAccessAt = LocalDateTime.now();
    }
}