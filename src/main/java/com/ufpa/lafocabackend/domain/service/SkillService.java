package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Skill;
import com.ufpa.lafocabackend.repository.SkillRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill save (Skill skill) {

        return skillRepository.save(skill);
    }

    public List<Skill> list (){

        return skillRepository.findAll();
    }

    public Skill read (Long skillId) {
        return getOrFail(skillId);
    }

    public Skill update (Skill skill) {

        return save(skill);
    }

    public void delete (Long skillsId) {

        try {
            skillRepository.deleteById(skillsId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), skillsId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), skillsId);
        }
    }

    private Skill getOrFail(Long skillsId) {
        return skillRepository.findById(skillsId)
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), skillsId));
    }
}
