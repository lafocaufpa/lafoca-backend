package com.ufpa.lafocabackend.domain.model.dto.output;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberSummaryDto {

    private String id;
    private String displayName;
    private String slug;
    private String function;
    private String photo;
}
