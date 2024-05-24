package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Skill;
import com.ufpa.lafocabackend.domain.model.dto.output.SkillDto;
import com.ufpa.lafocabackend.repository.SkillRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public SkillService(SkillRepository skillRepository, ModelMapper modelMapper) {
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    public SkillDto save (SkillDto skillDto) {
        final Skill skill = modelMapper.map(skillDto, Skill.class);
        return modelMapper.map(skillRepository.save(skill), SkillDto.class);
    }

    public List<SkillDto> list (){

        final Type type = new TypeToken<List<SkillDto>>() {

        }.getType();

        return modelMapper.map(skillRepository.findAll(), type);
    }

    public SkillDto read (Integer skillId) {
        return modelMapper.map(getOrFail(skillId), SkillDto.class) ;
    }

    public SkillDto update (Integer skillId, SkillDto newSkillDto) {

        final Skill currentSkill = getOrFail(skillId);
        modelMapper.map(newSkillDto, currentSkill);
        currentSkill.setSkillId(skillId);
        return modelMapper.map(skillRepository.save(currentSkill), SkillDto.class);
    }

    public void delete (Integer skillsId) {

        try {
            skillRepository.deleteById(skillsId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Skill.class.getSimpleName(), skillsId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Skill.class.getSimpleName(), skillsId);
        }
    }

    public Skill getOrFail(Integer skillsId) {
        return skillRepository.findById(skillsId)
                .orElseThrow( () -> new EntityNotFoundException(Skill.class.getSimpleName(), skillsId));
    }
}
