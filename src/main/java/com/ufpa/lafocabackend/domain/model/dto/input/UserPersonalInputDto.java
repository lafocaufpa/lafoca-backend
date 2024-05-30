package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPersonalInputDto {

        @NotBlank
        private String email;

        @NotBlank
        private String name;

        @NotNull
        private List<@NotNull Long> groups;

}
