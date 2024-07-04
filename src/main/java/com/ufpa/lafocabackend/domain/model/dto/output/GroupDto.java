package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GroupDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<PermissionDto> permissions;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<@Positive @NotNull Long> permissionsId;

}
