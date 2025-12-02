package pt.ipleiria.estg.dei.ei.daeproject.academics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;

import javax.management.DescriptorKey;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ROLE")
public class User extends Versionable {
    @Id
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String profilePicture;
    @NotBlank
    @Enumerated(EnumType.ORDINAL) // Makes the Enum be like 0 ,1 ,2 ... We could use also the EnumType.String that would make it like a string !!!
    private Status status; // Custom Enum class !!!

    public User() {}

    public User(String name, String password, String email,String profilePicture, Status status) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotBlank String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(@NotBlank String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
