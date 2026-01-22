package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

public class UserRefDTO {
    private int id;
    private String name;
    private String email;

    public UserRefDTO() {}
    public UserRefDTO(int id, String name, String email) {
        this.id = id;
        this.name = name;
    }

    public static UserRefDTO from(User user) {
        UserRefDTO userRefDTO = new UserRefDTO();
        userRefDTO.setId(user.getId());
        userRefDTO.setName(user.getName());
        userRefDTO.setEmail(user.getEmail());
        return userRefDTO;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
