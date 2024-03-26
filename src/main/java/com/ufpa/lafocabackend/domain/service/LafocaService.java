package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Lafoca;
import com.ufpa.lafocabackend.domain.model.dto.output.LafocaDto;
import com.ufpa.lafocabackend.repository.LafocaRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class LafocaService {

    private final LafocaRepository lafocaRepository;
    private final ModelMapper modelMapper;

    public LafocaService(LafocaRepository lafocaRepository, ModelMapper modelMapper) {
        this.lafocaRepository = lafocaRepository;
        this.modelMapper = modelMapper;
    }


    @Transactional
    public LafocaDto save(LafocaDto lafocaDto) {
        final Lafoca lafoca = modelMapper.map(lafocaDto, Lafoca.class);
        final Lafoca lafocaSaved = lafocaRepository.save(lafoca);
        return modelMapper.map(lafocaSaved, LafocaDto.class);
    }

    @Transactional
    public LafocaDto update(Long lafocaId, LafocaDto lafocaDto) {
        Lafoca lafoca = getOrFail(lafocaId);
        modelMapper.map(lafocaDto, lafoca);
        final Lafoca lafocaSaved = lafocaRepository.save(lafoca);
        return modelMapper.map(lafocaSaved, LafocaDto.class);
    }

    public List<LafocaDto> list() {
        final List<Lafoca> lafocaList = lafocaRepository.findAll();
        Type listType = new TypeToken<List<LafocaDto>>() {

        }.getType();

        return modelMapper.map(lafocaList, listType);
    }

    public LafocaDto read(Long lafocaId) {
        final Lafoca lafoca = getOrFail(lafocaId);
        return modelMapper.map(lafoca, LafocaDto.class);
    }

    public LafocaDto printCounts() {
        final LafocaRepository.CountResult counts = lafocaRepository.getCounts();

        return LafocaDto.builder()
                .totalArticles(counts.getTotalArticles())
                .totalProjects(counts.getTotalProjects())
                .totalTcc(counts.getTotalTccs())
                .totalStudents(counts.getTotalStudents())
                .build();
    }

    public void delete(Long lafocaId) {

        try {
            lafocaRepository.deleteById(lafocaId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), lafocaId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), lafocaId);
        }

    }

    private Lafoca getOrFail(Long lafocaId) {
        return lafocaRepository.findById(lafocaId)
                .orElseThrow(() -> new EntityNotFoundException(Lafoca.class.getSimpleName(), lafocaId));
    }
}
