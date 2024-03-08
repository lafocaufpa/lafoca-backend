package com.ufpa.lafocabackend.core.modelmapper;

import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.Student;
import com.ufpa.lafocabackend.domain.model.dto.FunctionStudentDto;
import com.ufpa.lafocabackend.domain.model.dto.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.PermissionDto;
import com.ufpa.lafocabackend.domain.model.dto.input.StudentInputDto;
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
                skip(destination.getId());
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

        modelMapper.addMappings(new PropertyMap<StudentInputDto, Student>() {
            @Override
            protected void configure() {
                skip(destination.getStudentId());
            }
        });


        return modelMapper;
    }
}

