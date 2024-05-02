package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInputDto {

    private String id;
    private String name;
    private String description;
    private String email;
    private String biography;
    private String linkPortifolio;
    private String linkLinkedin;
    private Long functionMemberId;
    private Long[] SkillsId;
    private TccDto tcc;
    private Long[] articles;
    private String[] projects;
}
