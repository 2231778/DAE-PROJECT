package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {
    @NotBlank
    @Email
    private String email;

    public EmailDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}