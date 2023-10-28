package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PhotoInputDto {

    private MultipartFile photo;
    private String fileName;
}
