package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PermissionDto {

    private Long id;

    private String name;

    private String description;
}
