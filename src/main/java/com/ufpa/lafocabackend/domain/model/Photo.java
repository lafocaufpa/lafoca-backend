package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public abstract class Photo {
    @Id
    @EqualsAndHashCode.Include
    private String photoId;

    private Long size;

    private String contentType;

    private String url;

    private String fileName ;

    public Photo() {
    }
}