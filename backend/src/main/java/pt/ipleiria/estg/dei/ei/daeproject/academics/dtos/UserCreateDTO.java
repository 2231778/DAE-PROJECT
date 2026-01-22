package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.RoleType;

public class UserCreateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Profile picture is required")
    private String profilePicture;

    @NotNull(message = "Role is required")
    private RoleType role; // "ADMIN", "COLABORADOR", "RESPONSAVEL"

    public UserCreateDTO() {}

    public UserCreateDTO(String name, String password, String email, String profilePicture, RoleType role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
}
