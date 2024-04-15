package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.FunctionMember;
import com.ufpa.lafocabackend.repository.FunctionMemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionMemberService {

    private final FunctionMemberRepository functionMemberRepository;

    public FunctionMemberService(FunctionMemberRepository functionMemberRepository) {
        this.functionMemberRepository = functionMemberRepository;
    }

    public FunctionMember save (FunctionMember functionMember) {

        return functionMemberRepository.save(functionMember);
    }

    public List<FunctionMember> list (){

        return functionMemberRepository.findAll();
    }

    public FunctionMember read (Long functionMemberId) {
        return getOrFail(functionMemberId);
    }


    public FunctionMember update (FunctionMember functionMember) {

        return save(functionMember);
    }

    public void delete (Long functionMemberId) {

        try {
            functionMemberRepository.deleteById(functionMemberId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), functionMemberId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), functionMemberId);
        }

    }

    private FunctionMember getOrFail(Long functionMemberId) {
        return functionMemberRepository.findById(functionMemberId)
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), functionMemberId));
    }
}
