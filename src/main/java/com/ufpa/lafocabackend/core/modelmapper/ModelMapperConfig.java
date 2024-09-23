package com.ufpa.lafocabackend.core.modelmapper;

import com.ufpa.lafocabackend.domain.model.*;
import com.ufpa.lafocabackend.domain.model.dto.input.TccInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.FunctionMemberDto;
import com.ufpa.lafocabackend.domain.model.dto.output.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.output.PermissionDto;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.SkillDto;
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
                skip(destination.getPermissions());
            }
        });

        modelMapper.addMappings(new PropertyMap<FunctionMemberDto, FunctionMember>() {
            @Override
            protected void configure() {
                skip(destination.getFunctionMemberId());
            }
        });

        modelMapper.addMappings(new PropertyMap<TccInputDto, Tcc>() {
            @Override
            protected void configure() {
                skip(destination.getLinesOfResearch());
            }
        });

        modelMapper.addMappings(new PropertyMap<MemberInputDto, Member>() {
            @Override
            protected void configure() {
                skip(destination.getMemberId());
                skip(destination.getTcc().getTccId());
                skip(destination.getFunctionMember());
                skip(destination.getSkills());
                skip(destination.getProjects());
                skip(destination.getArticles());
            }
        });

        modelMapper.addMappings(new PropertyMap<UserInputDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getUserId());
                skip(destination.getSlug());
                skip(destination.getDateRegister());
                skip(destination.getGroups());
            }
        });

        modelMapper.addMappings(new PropertyMap<SkillDto, Skill>() {

            @Override
            protected void configure() {
                skip(destination.getSkillPicture());
            }
        });

        return modelMapper;
    }
}

