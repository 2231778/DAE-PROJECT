package pt.ipleiria.estg.dei.ei.daeproject.academics.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetDTO {
    @NotBlank
    private String newPassword;

    public PasswordResetDTO() {}

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}