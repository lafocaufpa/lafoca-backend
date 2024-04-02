package com.ufpa.lafocabackend.domain.model.dto;

import com.ufpa.lafocabackend.domain.model.dto.input.TccDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class MemberDto {

    private Long memberId;
    private String name;
    private String description;
    private String email;
    private String biography;
    private String linkPortifolio;
    private OffsetDateTime dateRegister;
    private String urlPhoto;
    private FunctionStudentDto functionStudent;
    private List<SkillDto> skills;
    private TccDto tcc;
    private List<ArticleDto> articles;
    private List<ProjectDto> projects;
}
