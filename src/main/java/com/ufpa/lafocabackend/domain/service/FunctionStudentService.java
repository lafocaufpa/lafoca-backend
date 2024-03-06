package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import com.ufpa.lafocabackend.repository.FunctionStudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionStudentService {


    private final FunctionStudentRepository functionStudentRepository;

    public FunctionStudentService(FunctionStudentRepository functionStudentRepository) {
        this.functionStudentRepository = functionStudentRepository;
    }

    public FunctionStudent save (FunctionStudent functionStudent) {

        return functionStudentRepository.save(functionStudent);
    }

    public List<FunctionStudent> list (){

        return functionStudentRepository.findAll();
    }

    public FunctionStudent read (Long functionStudentId) {
        return getOrFail(functionStudentId);
    }


    public FunctionStudent update (FunctionStudent functionStudent) {

        return save(functionStudent);
    }

    public void delete (Long functionStudentId) {

        try {
            functionStudentRepository.deleteById(functionStudentId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), functionStudentId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), functionStudentId);
        }

    }

    private FunctionStudent getOrFail(Long functionStudentId) {
        return functionStudentRepository.findById(functionStudentId)
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), functionStudentId));
    }
}
