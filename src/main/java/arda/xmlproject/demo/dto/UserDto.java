package arda.xmlproject.demo.dto;

import arda.xmlproject.demo.entities.ApiPermissions;
import arda.xmlproject.demo.entities.UserRoles;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String username;

    // şifreyi response'a yazma
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;
    private String lastName;

    private LocalDateTime accountCreatedAt;
    private LocalDateTime lastAccessAt;

    private UserRoles role;
    private Set<ApiPermissions> permissions;
}