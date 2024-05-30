package com.ufpa.lafocabackend.domain.model.dto.output;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberSummaryDto {

    private String id;
    private String firstName;
    private String lastName;
    private String slug;
    private String function;
    private String photo;
}
