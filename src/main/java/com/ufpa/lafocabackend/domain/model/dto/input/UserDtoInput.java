package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDtoInput {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

}
