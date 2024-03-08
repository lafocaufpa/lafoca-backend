package com.ufpa.lafocabackend.domain.model.dto;

import com.ufpa.lafocabackend.domain.model.Skills;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class StudentDto {

    private Long id;
    private String name;
    private String description;
    private String email;
    private String biography;
    private String linkPortifolio;
    private OffsetDateTime dateRegister;
    private String urlPhoto;
    private FunctionStudentDto functionStudent;
    private List<Skills> skills;

}
