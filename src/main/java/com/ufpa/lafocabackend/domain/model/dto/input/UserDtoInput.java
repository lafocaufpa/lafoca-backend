package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class UserDtoInput {

    private String email;
    private String password;
    private String name;

}
