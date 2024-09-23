package com.ufpa.lafocabackend.domain.model.dto.input;

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
    private String fullName;

    @NotBlank
    private String displayName;

    @NotBlank
    private String description;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String biography;

    private String linkPortifolio;

    private String linkLinkedin;

    private Long functionMemberId;

    private Integer yearClassId;

    private List<@Positive @NotNull Long> skillsId;

    private Long tccId;

    private List<@NotNull Long> articlesId;

    private List<@NotBlank String> projectsId;
}
