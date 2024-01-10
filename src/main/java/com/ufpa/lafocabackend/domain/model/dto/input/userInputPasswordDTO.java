package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class userInputPasswordDTO {

    private String currentPassword;

    private String newPassword;
}
