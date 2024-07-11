package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.domain.model.dto.input.TccInputDto;
import com.ufpa.lafocabackend.repository.TccRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TccService {

    private final TccRepository tccRepository;
    private final LineOfResearchService lineOfResearchService;
    private final ModelMapper modelMapper;


    public TccService(TccRepository tccRepository, LineOfResearchService lineOfResearchService, ModelMapper modelMapper) {
        this.tccRepository = tccRepository;
        this.lineOfResearchService = lineOfResearchService;
        this.modelMapper = modelMapper;
    }

    public Tcc save (TccInputDto tccInputDto) {
        final Tcc tcc = modelMapper.map(tccInputDto, Tcc.class);

        List<LineOfResearch> lineOfResearches = new java.util.ArrayList<>(Collections.emptyList());

        for (String lineOfResearchId : tccInputDto.getLineOfResearchIds()) {
            LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
            lineOfResearches.
                    add(lineOfResearch);
        }
        tcc.setLinesOfResearch(lineOfResearches);
        return tccRepository.save(tcc);
    }

    public Page<Tcc> list (Pageable pageable){

        return tccRepository.findAll(pageable);
    }

    public Page<Tcc> list(String title, String lineOfResearchId, Pageable pageable) {
        if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return tccRepository.findByNameContainingAndLineOfResearchId(title, lineOfResearchId, pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            lineOfResearchService.exist(lineOfResearchId);
            return tccRepository.findByLineOfResearchId(lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty()) {
            return tccRepository.findByNameContaining(title, pageable);
        } else {
            return tccRepository.findAll(pageable);
        }
    }

    public Tcc read (Long tccId) {
        return getOrFail(tccId);
    }

    @Transactional
    public Tcc update (Long tccId, TccInputDto tccInputDto) {

        Tcc currentTcc = read(tccId);

        modelMapper.map(tccInputDto, currentTcc);
        currentTcc.setTccId(tccId);

        List<LineOfResearch> linesOfResearches = Collections.emptyList();
        if(tccInputDto.getLineOfResearchIds() != null) {
            for (String lineOfResearchId : tccInputDto.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                linesOfResearches.add(lineOfResearch);
            }
        }

        currentTcc.setLinesOfResearch(linesOfResearches);

        return currentTcc;
    }

    public void delete (Long tccId) {

        try {
            tccRepository.deleteById(tccId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Tcc.class.getSimpleName(), tccId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Tcc.class.getSimpleName(), tccId);
        }

    }
    private Tcc getOrFail(Long tccId) {
        return tccRepository.findById(tccId)
                .orElseThrow( () -> new EntityNotFoundException(Tcc.class.getSimpleName(), tccId));
    }
}
