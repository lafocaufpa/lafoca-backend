package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class SkillPicture {

    @Id
    @EqualsAndHashCode.Include
    private Long skillPictureId;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 15)
    private String contentType;

    @Column(length = 700)
    private String url;

    @Column(nullable = false, length = 225)
    private String fileName ;

    @Column(length = 40)
    private String dataUpdate;
}

