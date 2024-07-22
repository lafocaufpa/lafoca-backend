package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class MemberResumed {

    private String id;
    private String fullName;
    private String displayName;
    private String photo;
    private String function;
    private String slug;
    private String email;
    private Integer yearClass;
    private OffsetDateTime dateRegister;
}
