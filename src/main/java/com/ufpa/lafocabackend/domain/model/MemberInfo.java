package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class MemberInfo implements Serializable {
    private String name;
    private String slug;

    public MemberInfo() {
    }

}