package com.ufpa.lafocabackend.core.modelmapper;

import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper () {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(NewsInputDto.class, News.class)
                .addMappings
                        (mapping -> mapping.skip(NewsInputDto::getUserId, News::setNewsId));
        return modelMapper;
    }
}
