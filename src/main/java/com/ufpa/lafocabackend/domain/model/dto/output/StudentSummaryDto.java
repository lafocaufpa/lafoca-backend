package com.ufpa.lafocabackend.domain.model.dto.output;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentSummaryDto {

    private Long id;
    private String name;
    private String function;
    private String photo;
}
