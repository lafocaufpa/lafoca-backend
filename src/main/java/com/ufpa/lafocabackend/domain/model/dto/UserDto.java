package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {

    private String userId;
    private String name;
    private String email;
    private String urlPhoto;
    private Set<GroupDto> groups;
}
