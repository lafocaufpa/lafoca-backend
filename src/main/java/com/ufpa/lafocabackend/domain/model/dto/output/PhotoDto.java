package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Setter
public class PhotoDto {

    @JsonProperty(access = READ_ONLY)
    private String id;

    @JsonProperty(access = READ_ONLY)
    private String fileName;

    @JsonProperty(access = READ_ONLY)
    private Long size;

    @JsonProperty(access = READ_ONLY)
    private String contentType;

    @JsonProperty(access = READ_ONLY)
    private String url;

}
