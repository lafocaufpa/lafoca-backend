package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemberInputDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String description;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String biography;

    private String linkPortifolio;

    private String linkLinkedin;

    @NotNull
    private Long functionMemberId;

    @NotNull
    private List<@Positive @NotNull Integer> skillsId;

    @NotNull
    @Valid
    private TccInputDto tcc;

    @NotNull
    private List<@NotNull Long> articlesId;

    @NotNull
    private List<@NotBlank String> projectsId;
}
