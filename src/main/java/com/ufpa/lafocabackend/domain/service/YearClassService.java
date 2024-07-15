package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.YearClass;
import com.ufpa.lafocabackend.domain.model.dto.YearClassDTO;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
import com.ufpa.lafocabackend.repository.MemberRepository;
import com.ufpa.lafocabackend.repository.YearClassRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YearClassService {

    private final YearClassRepository yearClassRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;

    public YearClassService(YearClassRepository yearClassRepository, ModelMapper modelMapper, MemberRepository memberRepository) {
        this.yearClassRepository = yearClassRepository;
        this.modelMapper = modelMapper;
        this.memberRepository = memberRepository;
    }

    public YearClassDTO save(YearClassDTO yearClassDto) {
        final YearClass yearClass = modelMapper.map(yearClassDto, YearClass.class);
        return modelMapper.map(yearClassRepository.save(yearClass), YearClassDTO.class);
    }

    public Page<YearClassDTO> list(Pageable pageable) {
        final Type type = new TypeToken<List<YearClassDTO>>() {}.getType();
        Page<YearClass> yearClasses = yearClassRepository.findAll(pageable);
        List<YearClassDTO> map = modelMapper.map(yearClasses.getContent(), type);
        return new PageImpl<>(map, pageable, yearClasses.getTotalElements());
    }

    public YearClassDTO read(Long yearClassId) {
        return modelMapper.map(getOrFail(yearClassId), YearClassDTO.class);
    }

    public YearClassDTO update(Long yearClassId, YearClassDTO newYearClassDto) {
        final YearClass currentYearClass = getOrFail(yearClassId);
        modelMapper.map(newYearClassDto, currentYearClass);
        currentYearClass.setYearClassId(yearClassId);
        return modelMapper.map(yearClassRepository.save(currentYearClass), YearClassDTO.class);
    }

    public void delete(Long yearClassId) {
        try {
            yearClassRepository.deleteById(yearClassId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(YearClass.class.getSimpleName(), yearClassId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(YearClass.class.getSimpleName(), yearClassId);
        }
    }

    public YearClass getOrFail(Long yearClassId) {
        return yearClassRepository.findById(yearClassId)
                .orElseThrow(() -> new EntityNotFoundException(YearClass.class.getSimpleName(), yearClassId));
    }

    public List<YearClassDTO> listWithoutPagination() {
        List<YearClass> classes = yearClassRepository.findAll();
        final Type type = new TypeToken<List<YearClassDTO>>() {}.getType();
        return modelMapper.map(classes, type);
    }

    public Page<MemberResumed> listMembersByYearClass(Long yearClassId, String name, Pageable pageable) {
        // Busca pela YearClass específica
        YearClass yearClass = yearClassRepository.findById(yearClassId)
                .orElseThrow(() -> new EntityNotFoundException("Ano não encontrado", yearClassId));

        // Se o parâmetro 'name' for fornecido, busca por membros pelo nome dentro da YearClass
        if (name != null && !name.isEmpty()) {
            return findMembersByNameAndYearClass(name, yearClassId, pageable);
        } else {
            return findMembersByYearClass(yearClassId, pageable);
        }
    }

    private Page<MemberResumed> findMembersByYearClass(Long yearClass, Pageable pageable) {
        return memberRepository.findResumedMembersByYearClass(yearClass, pageable);
    }

    private Page<MemberResumed> findMembersByNameAndYearClass(String name, Long yearClass, Pageable pageable) {
        return memberRepository.findResumedMembersByFullNameContainingAndYearClass(name, yearClass, pageable);
    }

}
