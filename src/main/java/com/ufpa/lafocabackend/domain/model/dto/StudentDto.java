package com.ufpa.lafocabackend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@JsonPropertyOrder({"studentId", "name", "email", "biography", "dateRegister", "urlPhoto"})
public class StudentDto {

    @JsonIgnore
    private Long studentId;
    private String name;
    private String email;
    private String biography;
    @JsonIgnore
    private OffsetDateTime dateRegister;
    @JsonIgnore
    private String urlPhoto;

    // Getters and setters

    @JsonProperty("dateRegister")
    public OffsetDateTime getDateRegister() {
        return dateRegister;
    }

    @JsonProperty("dateRegister")
    public void setDateRegister(OffsetDateTime dateRegister) {
        this.dateRegister = dateRegister;
    }

    @JsonProperty("urlPhoto")
    public String getUrlPhoto() {
        return urlPhoto;
    }

    @JsonProperty("urlPhoto")
    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    @JsonProperty("studentId")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("studentId")
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
