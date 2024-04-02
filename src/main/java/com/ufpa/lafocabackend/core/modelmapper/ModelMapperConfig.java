package com.ufpa.lafocabackend.core.modelmapper;

import com.ufpa.lafocabackend.domain.model.*;
import com.ufpa.lafocabackend.domain.model.dto.FunctionStudentDto;
import com.ufpa.lafocabackend.domain.model.dto.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.PermissionDto;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper () {
        final ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<PermissionDto, Permission>() {
            @Override
            protected void configure() {
                skip(destination.getPermissionId());
            }
        });

        modelMapper.addMappings(new PropertyMap<GroupDto, Group>() {
            @Override
            protected void configure() {
                skip(destination.getGroupId());
            }
        });

        modelMapper.addMappings(new PropertyMap<FunctionStudentDto, FunctionStudent>() {
            @Override
            protected void configure() {
                skip(destination.getFunctionStudentId());
            }
        });

        modelMapper.addMappings(new PropertyMap<MemberInputDto, Member>() {
            @Override
            protected void configure() {
                skip(destination.getMemberId());
                skip(destination.getTcc().getTccId());
                skip(destination.getFunctionStudent());
                skip(destination.getSkills());
                skip(destination.getProjects());
                skip(destination.getArticles());
            }
        });

        return modelMapper;
    }
}

