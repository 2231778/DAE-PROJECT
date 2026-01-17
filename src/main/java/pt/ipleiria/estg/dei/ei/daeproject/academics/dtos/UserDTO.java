package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.daeproject.academics.Enums.Status;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Administrador;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Colaborador;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Responsavel;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String profilePicture;
    private Status status;
    private String role;
    private List<CommentDTO> comments;
    //TODO: SEE IF I PUT PUBLICATION OF USERS HERE

    public UserDTO() {
        this.comments = new ArrayList<CommentDTO>();
    }

    public UserDTO(Integer id, String name, String email, String profilePicture, Status status, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.status = status;
        this.role = role;
        this.comments = new ArrayList<CommentDTO>();
    }

    public static UserDTO from(User user) {
        String role;
        if (user instanceof Administrador) role = "ADMIN";
        else if (user instanceof Colaborador) role = "COLABORADOR";
        else if (user instanceof Responsavel) role = "RESPONSAVEL";
        else role = "USER";

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getStatus(),
                role
        );
        if (Hibernate.isInitialized(user.getComments())) {
            dto.comments = user.getComments()
                    .stream()
                    .map(CommentDTO::from)
                    .collect(Collectors.toList());
        }
        return dto;
    }

    public static List<UserDTO> from(List<User> users) {
        return users.stream().map(UserDTO::from).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
