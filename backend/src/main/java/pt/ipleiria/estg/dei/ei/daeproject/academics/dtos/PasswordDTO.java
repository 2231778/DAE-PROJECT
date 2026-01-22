package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import jakarta.validation.constraints.NotBlank;

public class PasswordDTO {
    @NotBlank
    private String newPassword;

    public PasswordDTO() {

    }
    public PasswordDTO(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
