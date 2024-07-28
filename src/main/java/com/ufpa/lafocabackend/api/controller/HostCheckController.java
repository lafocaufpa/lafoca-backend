package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.database.DbInfo;
import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.AllSystem;
import com.ufpa.lafocabackend.infrastructure.service.AllSystemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@RestController
@RequestMapping("/host-check")
public class HostCheckController {

    private final AllSystemService allSystemService;
    private final DbInfo dbInfo;

    public HostCheckController(AllSystemService allSystemService, DbInfo dbInfo) {
        this.allSystemService = allSystemService;
        this.dbInfo = dbInfo;
    }

    @CheckSecurityPermissionMethods.isAuthenticated
    @GetMapping
    public ResponseEntity<AllSystem> checkHost() {
        AllSystem allSystem = allSystemService.getSystemInfo();
        return LafocaCacheUtil.createCachedResponseHostCheck(allSystem);
    }

    @CheckSecurityPermissionMethods.Admin
    @PutMapping
    public ResponseEntity<AllSystem> updateSystemInfo(@RequestBody AllSystem manualInfo) {
        AllSystem updatedSystem = allSystemService.updateSystemInfo(manualInfo);
        return LafocaCacheUtil.createCachedResponseHostCheck(updatedSystem);
    }

    @CheckSecurityPermissionMethods.Admin
    @GetMapping("/backup")
    public ResponseEntity<StreamingResponseBody> backupDatabase() {
        File backupFile = null;
        try {
            backupFile = dbInfo.backupDatabase();
            allSystemService.updateLastBackup();

            if (backupFile != null && backupFile.exists()) {
                File finalBackupFile = backupFile;
                InputStream inputStream = new FileInputStream(finalBackupFile);

                StreamingResponseBody responseBody = outputStream -> {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();

                    dbInfo.fileDeletion(finalBackupFile.toPath());
                };

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backupFile.getName());

                return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @CheckSecurityPermissionMethods.Admin
    @PostMapping("/backup")
    public ResponseEntity<String> importDatabase(@RequestParam("file") MultipartFile file) {
        try {
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".sql")) {
                throw new Exception("O arquivo não é um .sql válido.");
            }

            Path tempFilePath = Files.createTempFile(null, ".sql");
            Files.write(tempFilePath, file.getBytes());

            File tempFile = tempFilePath.toFile();
            boolean result = dbInfo.importDatabase(tempFile);

            Files.delete(tempFilePath);

            if (result) {
                return ResponseEntity.ok("Importação bem-sucedida.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha na importação.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
