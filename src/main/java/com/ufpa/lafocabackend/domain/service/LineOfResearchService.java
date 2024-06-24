package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.dto.output.LineOfResearchDto;
import com.ufpa.lafocabackend.repository.LineOfResearchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineOfResearchService {

    private final LineOfResearchRepository lineOfResearchRepository;
    private final ModelMapper modelMapper;

    public LineOfResearchService(LineOfResearchRepository lineOfResearchRepository, ModelMapper modelMapper) {
        this.lineOfResearchRepository = lineOfResearchRepository;
        this.modelMapper = modelMapper;
    }

    public LineOfResearch save (LineOfResearchDto lineOfResearchDto) {

        return lineOfResearchRepository.save(modelMapper.map(lineOfResearchDto, LineOfResearch.class));
    }

    public Page<LineOfResearch> list(Pageable pageable) {
        return lineOfResearchRepository.findAll(pageable);
    }

    public Page<LineOfResearch> searchByName(String name, Pageable pageable) {
        return lineOfResearchRepository.findByNameContaining(name, pageable);
    }

    public LineOfResearch read (String lineOfResearchId) {
        return getOrFail(lineOfResearchId);
    }

    public LineOfResearch update (String lineOfResearchId, LineOfResearchDto newLineOfResearch) {

        final LineOfResearch currentLineOfResearch = read(lineOfResearchId);

        modelMapper.map(newLineOfResearch, currentLineOfResearch);
        currentLineOfResearch.setLineOfResearchId(lineOfResearchId);

        return lineOfResearchRepository.save(currentLineOfResearch);
    }

    public void delete (String lineOfResearchId) {

        try {
            lineOfResearchRepository.deleteById(lineOfResearchId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(LineOfResearch.class.getSimpleName(), lineOfResearchId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(LineOfResearch.class.getSimpleName(), lineOfResearchId);
        }

    }

    private LineOfResearch getOrFail(String lineOfResearchId) {
        return lineOfResearchRepository.findById(lineOfResearchId)
                .orElseThrow( () -> new EntityNotFoundException(LineOfResearch.class.getSimpleName(), lineOfResearchId));
    }
}
