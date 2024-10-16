package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.domain.model.dto.input.TccInputDto;
import com.ufpa.lafocabackend.repository.MemberRepository;
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
import java.util.Optional;

@Service
public class TccService {

    private final TccRepository tccRepository;
    private final LineOfResearchService lineOfResearchService;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;

    public TccService(TccRepository tccRepository, LineOfResearchService lineOfResearchService, ModelMapper modelMapper, MemberRepository memberRepository) {
        this.tccRepository = tccRepository;
        this.lineOfResearchService = lineOfResearchService;
        this.modelMapper = modelMapper;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Tcc save (TccInputDto tccInputDto) {
        final Tcc tcc = modelMapper.map(tccInputDto, Tcc.class);

        List<LineOfResearch> lineOfResearches = new java.util.ArrayList<>(Collections.emptyList());

        for (String lineOfResearchId : tccInputDto.getLineOfResearchIds()) {
            LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
            lineOfResearches.
                    add(lineOfResearch);
        }
        tcc.setLinesOfResearch(lineOfResearches);
        Tcc tccSaved = tccRepository.save(tcc);

        if(tccInputDto.getSlugMember() != null) {
            Member member = memberRepository.findBySlug(tccInputDto.getSlugMember()).
                    orElseThrow(() -> new EntityNotFoundException(Member.class.getSimpleName(), tccInputDto.getSlugMember()));
            tccSaved.setSlugMember(member.getSlug());
            tccSaved.setNameMember(member.getFullName());
            member.setTcc(tccSaved);
            memberRepository.save(member);
        }

        return tccSaved ;
    }

    public Page<Tcc> list (Pageable pageable){

        return tccRepository.findAll(pageable);
    }

    public Page<Tcc> list(String title, String lineOfResearchId, Integer year, Pageable pageable) {
        if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return tccRepository.findByNameContainingAndLineOfResearchIdAndYear(title, lineOfResearchId, year, pageable);
        } else if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return tccRepository.findByNameContainingAndLineOfResearchId(title, lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty() && year != null) {
            return tccRepository.findByNameContainingAndYear(title, year, pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return tccRepository.findByLineOfResearchIdAndYear(lineOfResearchId, year, pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return tccRepository.findByLineOfResearchId(lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty()) {
            return tccRepository.findByNameContaining(title, pageable);
        } else if (year != null) {
            return tccRepository.findByYear(year, pageable);
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

        List<LineOfResearch> linesOfResearches = new ArrayList<>(Collections.emptyList());
        if(tccInputDto.getLineOfResearchIds() != null) {
            for (String lineOfResearchId : tccInputDto.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                linesOfResearches.add(lineOfResearch);
            }
        }

        currentTcc.setLinesOfResearch(linesOfResearches);

        if(tccInputDto.getSlugMember() != null) {

            Optional<Member> memberByTcc = tccRepository.findMemberByTcc(tccId);

            if(memberByTcc.isPresent()) {
                memberByTcc.get().setTcc(null);
                memberRepository.save(memberByTcc.get());
            }

            Member member = memberRepository.findBySlug(tccInputDto.getSlugMember()).
                    orElseThrow(() -> new EntityNotFoundException(Member.class.getSimpleName(), tccInputDto.getSlugMember()));
            currentTcc.setSlugMember(member.getSlug());
            currentTcc.setNameMember(member.getFullName());
            member.setTcc(currentTcc);
            memberRepository.save(member);
        } else {
            Optional<Member> memberByTcc = tccRepository.findMemberByTcc(tccId);

            if(memberByTcc.isPresent()) {
                memberByTcc.get().setTcc(null);
                currentTcc.setNameMember(null);
                currentTcc.setSlugMember(null);

                memberRepository.save(memberByTcc.get());
            }
        }

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
