package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInputDto {

    private String name;

    @NotNull
    private String description;
    private String email;
    private String biography;
    private String linkPortifolio;
    private String linkLinkedin;
    private Long functionMemberId;
    private Long[] SkillsId;
    private TccDto tcc;
    private Long[] articlesId;
    private String[] projectsId;
}
