package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.Column;
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

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 15)
    private String contentType;

    @Column(length = 700)
    private String url;

    @Column(nullable = false, length = 225)
    private String fileName ;

    public Photo() {
    }
}