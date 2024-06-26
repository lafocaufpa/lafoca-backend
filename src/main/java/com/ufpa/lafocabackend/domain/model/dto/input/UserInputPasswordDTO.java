package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInputPasswordDTO {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}
