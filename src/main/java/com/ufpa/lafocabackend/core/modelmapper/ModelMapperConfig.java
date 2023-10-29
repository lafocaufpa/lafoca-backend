package com.ufpa.lafocabackend.core.modelmapper;

import com.ufpa.lafocabackend.domain.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper () {

        ModelMapper modelMapper = new ModelMapper();

        return modelMapper;
    }
}
