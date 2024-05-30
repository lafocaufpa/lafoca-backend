package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufpa.lafocabackend.domain.model.dto.input.TccInputDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class MemberDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    private String firstName;
    private String lastName;
    private String slug;
    private String description;
    private String email;
    private String biography;
    private String linkPortifolio;
    private String linkLinkedin;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime dateRegister;
    private String urlPhoto;
    private FunctionMemberDto functionMember;
    private List<SkillDto> skills;
    private TccInputDto tcc;
    private List<ArticleDto> articles;
    private List<ProjectDto> projects;
}
