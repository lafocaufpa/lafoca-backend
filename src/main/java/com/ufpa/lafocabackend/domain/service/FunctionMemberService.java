package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.FunctionMember;
import com.ufpa.lafocabackend.repository.FunctionMemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FunctionMemberService {

    private final FunctionMemberRepository functionMemberRepository;

    public FunctionMemberService(FunctionMemberRepository functionMemberRepository) {
        this.functionMemberRepository = functionMemberRepository;
    }

    public FunctionMember save (FunctionMember functionMember) {

        return functionMemberRepository.save(functionMember);
    }

    public Page<FunctionMember> list(Pageable pageable) {
        return functionMemberRepository.findAll(pageable);
    }

    public Page<FunctionMember> searchByName(String name, Pageable pageable) {
        return functionMemberRepository.findByNameContaining(name, pageable);
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
            throw new EntityInUseException(FunctionMember.class.getSimpleName(), functionMemberId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(FunctionMember.class.getSimpleName(), functionMemberId);
        }

    }

    private FunctionMember getOrFail(Long functionMemberId) {
        return functionMemberRepository.findById(functionMemberId)
                .orElseThrow( () -> new EntityNotFoundException(FunctionMember.class.getSimpleName(), functionMemberId));
    }
}
