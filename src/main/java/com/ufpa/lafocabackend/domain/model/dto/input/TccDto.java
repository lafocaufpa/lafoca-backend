package com.ufpa.lafocabackend.domain.model.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TccDto {

    private Long id;
    private String name;
    private String url;
    private String date;
    private Long studentId;

    @JsonProperty("student_id")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("student_id")
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
