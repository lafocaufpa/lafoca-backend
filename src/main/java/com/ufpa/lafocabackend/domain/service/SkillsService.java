package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Skills;
import com.ufpa.lafocabackend.repository.SkillsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillsService {

    private final SkillsRepository skillsRepository;

    public SkillsService(SkillsRepository skillsRepository) {
        this.skillsRepository = skillsRepository;
    }

    public Skills save (Skills skills) {

        return skillsRepository.save(skills);
    }

    public List<Skills> list (){

        return skillsRepository.findAll();
    }

    public Skills read (Long skillsId) {
        return getOrFail(skillsId);
    }

    public Skills update (Skills skills) {

        return save(skills);
    }

    public void delete (Long skillsId) {

        try {
            skillsRepository.deleteById(skillsId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), skillsId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), skillsId);
        }

    }

    private Skills getOrFail(Long skillsId) {
        return skillsRepository.findById(skillsId)
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), skillsId));
    }
}
