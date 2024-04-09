package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDtoInput {

    private String email;
    private String password;
    private String name;

}
