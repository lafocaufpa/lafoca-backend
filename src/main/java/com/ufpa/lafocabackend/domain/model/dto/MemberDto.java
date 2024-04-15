package com.ufpa.lafocabackend.domain.model.dto;

import com.ufpa.lafocabackend.domain.model.dto.input.TccDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class MemberDto {

    private String memberId;
    private String name;
    private String slug;
    private String description;
    private String email;
    private String biography;
    private String linkPortifolio;
    private OffsetDateTime dateRegister;
    private String urlPhoto;
    private FunctionMemberDto functionMember;
    private List<SkillDto> skills;
    private TccDto tcc;
    private List<ArticleDto> articles;
    private List<ProjectDto> projects;
}
