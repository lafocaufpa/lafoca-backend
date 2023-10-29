package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Photo {

    @Id
    @EqualsAndHashCode.Include
    private Long photoId;
    
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long size;
    
    @Column(nullable = false)
    private String contentType;

}
