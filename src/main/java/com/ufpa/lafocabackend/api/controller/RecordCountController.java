package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.CacheUtil;
import com.ufpa.lafocabackend.domain.model.RecordCount;
import com.ufpa.lafocabackend.domain.model.dto.input.RecordCountDTO;
import com.ufpa.lafocabackend.domain.service.RecordCountService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/record-count")
public class RecordCountController {

    private final RecordCountService recordCountService;

    public RecordCountController(RecordCountService recordCountService) {
        this.recordCountService = recordCountService;
    }

    @GetMapping
    public ResponseEntity<RecordCountDTO> getRecordCount() {
        return recordCountService.getRecordCount()
                .map(recordCount -> {
                    RecordCountDTO dto = new RecordCountDTO();
                    dto.setArticles(recordCount.getArticles());
                    dto.setTccs(recordCount.getTccs());
                    dto.setProjects(recordCount.getProjects());
                    dto.setMembers(recordCount.getMembers());
                    return CacheUtil.createCachedResponseRecordCount(dto);
                })
                .orElse(CacheUtil.createCachedResponseNoContentRecordCount());
    }

    @CheckSecurityPermissionMethods.User.Level1
    @PutMapping
    public ResponseEntity<RecordCountDTO> updateRecordCount(@RequestBody RecordCountDTO recordCountDTO) {
        RecordCount updatedRecordCount = recordCountService.updateRecordCount(recordCountDTO);
        RecordCountDTO responseDto = new RecordCountDTO();
        responseDto.setArticles(updatedRecordCount.getArticles());
        responseDto.setTccs(updatedRecordCount.getTccs());
        responseDto.setProjects(updatedRecordCount.getProjects());
        responseDto.setMembers(updatedRecordCount.getMembers());
        return ResponseEntity.ok(responseDto);
    }
}