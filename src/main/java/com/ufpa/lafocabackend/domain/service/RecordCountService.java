package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.RecordCount;
import com.ufpa.lafocabackend.domain.model.dto.input.RecordCountDTO;
import com.ufpa.lafocabackend.repository.RecordCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RecordCountService {

    private final RecordCountRepository recordCountRepository;

    public RecordCountService(RecordCountRepository recordCountRepository) {
        this.recordCountRepository = recordCountRepository;
    }

    public Optional<RecordCount> getRecordCount() {
        return recordCountRepository.findById(1L);
    }

    @Transactional
    public RecordCount updateRecordCount(RecordCountDTO recordCountDTO) {
        RecordCount recordCount = recordCountRepository.findById(1L).orElse(new RecordCount());
        recordCount.setArticles(recordCountDTO.getArticles());
        recordCount.setTccs(recordCountDTO.getTccs());
        recordCount.setProjects(recordCountDTO.getProjects());
        recordCount.setMembers(recordCountDTO.getMembers());
        return recordCountRepository.save(recordCount);
    }
}