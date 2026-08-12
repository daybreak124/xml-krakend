package arda.xmlproject.demo.dto;

import arda.xmlproject.demo.entities.ApiPermissions;
import arda.xmlproject.demo.entities.UserRoles;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // @Size(min = 11, message = "Kimlik numarası en az 11 karakter olmalı")
    private Long ssn;

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;

    // şifreyi response'a yazma
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 5, message = "Şifre en az 5 karakter olmalı")
    private String password;

    @NotBlank(message = "İsim boş olamaz")
    private String name;

    @NotBlank(message = "Soyadı boş olamaz")
    private String lastName;

    private LocalDateTime accountCreatedAt;
    private LocalDateTime lastAccessAt;

    private UserRoles role;
    private Set<ApiPermissions> permissions;
}