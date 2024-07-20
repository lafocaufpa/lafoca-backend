package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.AllSystem;
import com.ufpa.lafocabackend.domain.service.DbInfo;
import com.ufpa.lafocabackend.repository.AllSystemRepository;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Optional;

@RestController
@RequestMapping("/host-check")
public class HostCheckController {

    private final AllSystemRepository allSystemRepository;
    private final DbInfo dbInfo;

    public HostCheckController(AllSystemRepository allSystemRepository, DbInfo dbInfo) {
        this.allSystemRepository = allSystemRepository;
        this.dbInfo = dbInfo;
    }

    @CheckSecurityPermissionMethods.isAuthenticated
    @GetMapping
    public ResponseEntity<AllSystem> checkHost() {
        Optional<AllSystem> allSystemOptional = allSystemRepository.findById(1L);

        AllSystem allSystem = allSystemOptional.orElseGet(() -> {
            AllSystem newSystem = new AllSystem();
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                newSystem.setBackendHostAddress(localHost.getHostAddress());
            } catch (Exception e) {
                newSystem.setBackendHostAddress("Unknown");
            }

            try {
                newSystem.setBackendJavaVersion(System.getProperty("java.version"));
            } catch (Exception e) {
                newSystem.setBackendJavaVersion("Unknown");
            }

            try {
                newSystem.setBackendSpringBootVersion(SpringBootVersion.getVersion());
            } catch (Exception e) {
                newSystem.setBackendSpringBootVersion("Unknown");
            }

            try {
                newSystem.setBackendSpringVersion(SpringVersion.getVersion());
            } catch (Exception e) {
                newSystem.setBackendSpringVersion("Unknown");
            }

            try {
                newSystem.setBackendSpringSecurityCoreVersion(SpringSecurityCoreVersion.getVersion());
            } catch (Exception e) {
                newSystem.setBackendSpringSecurityCoreVersion("Unknown");
            }

            newSystem.setDbName(dbInfo.getDatabaseName());
            newSystem.setDbVersion(dbInfo.getDatabaseVersion());
            newSystem.setDbStatus(dbInfo.getDatabaseStatus());
            newSystem.setDbLastBackup(dbInfo.getLastBackup());
            newSystem.setId(1L);
            return allSystemRepository.save(newSystem);
        });

        return LafocaCacheUtil.createCachedResponseHostCheck(allSystem);
    }

    @CheckSecurityPermissionMethods.Admin
    @PutMapping
    public ResponseEntity<AllSystem> getSystemInfo(@RequestBody AllSystem manualInfo) {
        AllSystem allSystem = allSystemRepository.findById(1L).orElse(new AllSystem());
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            allSystem.setBackendHostAddress(localHost.getHostAddress());
        } catch (Exception e) {
            allSystem.setBackendHostAddress("Unknown");
        }

        try {
            allSystem.setBackendJavaVersion(System.getProperty("java.version"));
        } catch (Exception e) {
            allSystem.setBackendJavaVersion("Unknown");
        }

        try {
            allSystem.setBackendSpringBootVersion(SpringBootVersion.getVersion());
        } catch (Exception e) {
            allSystem.setBackendSpringBootVersion("Unknown");
        }

        try {
            allSystem.setBackendSpringVersion(SpringVersion.getVersion());
        } catch (Exception e) {
            allSystem.setBackendSpringVersion("Unknown");
        }

        try {
            allSystem.setBackendSpringSecurityCoreVersion(SpringSecurityCoreVersion.getVersion());
        } catch (Exception e) {
            allSystem.setBackendSpringSecurityCoreVersion("Unknown");
        }

        // Adiciona as informações manuais
        allSystem.setBackendHostName(manualInfo.getBackendHostName());
        allSystem.setBackendRamMemory(manualInfo.getBackendRamMemory());
        allSystem.setBackendStorage(manualInfo.getBackendStorage());
        allSystem.setBackendProcessorInfo(manualInfo.getBackendProcessorInfo());
        allSystem.setBackendOsDistributor(manualInfo.getBackendOsDistributor());
        allSystem.setBackendOsDescription(manualInfo.getBackendOsDescription());
        allSystem.setBackendOsRelease(manualInfo.getBackendOsRelease());
        allSystem.setBackendOsCodename(manualInfo.getBackendOsCodename());

        // Informações do frontend
        allSystem.setFrontendNodeVersion(manualInfo.getFrontendNodeVersion());
        allSystem.setFrontendNpmVersion(manualInfo.getFrontendNpmVersion());
        allSystem.setFrontendReactVersion(manualInfo.getFrontendReactVersion());
        allSystem.setFrontendNextjsVersion(manualInfo.getFrontendNextjsVersion());
        allSystem.setFrontendNextauthVersion(manualInfo.getFrontendNextauthVersion());
        allSystem.setFrontendHostName(manualInfo.getFrontendHostName());
        allSystem.setFrontendHostAddress(manualInfo.getFrontendHostAddress());
        allSystem.setFrontendRamMemory(manualInfo.getFrontendRamMemory());
        allSystem.setFrontendStorage(manualInfo.getFrontendStorage());
        allSystem.setFrontendProcessorInfo(manualInfo.getFrontendProcessorInfo());
        allSystem.setFrontendOsDistributor(manualInfo.getFrontendOsDistributor());
        allSystem.setFrontendOsDescription(manualInfo.getFrontendOsDescription());
        allSystem.setFrontendOsRelease(manualInfo.getFrontendOsRelease());
        allSystem.setFrontendOsCodename(manualInfo.getFrontendOsCodename());

        // Informações do banco de dados
        allSystem.setDbName(dbInfo.getDatabaseName());
        allSystem.setDbVersion(dbInfo.getDatabaseVersion());
        allSystem.setDbStatus(dbInfo.getDatabaseStatus());
        allSystem.setDbLastBackup(dbInfo.getLastBackup());
        allSystem.setId(1L);
        allSystemRepository.save(allSystem);

        return LafocaCacheUtil.createCachedResponseHostCheck(allSystem);
    }

    @CheckSecurityPermissionMethods.Admin
    @GetMapping("/backup")
    public ResponseEntity<StreamingResponseBody> backupDatabase() {
        try {
            File backupFile = dbInfo.backupDatabase();

            if (backupFile != null && backupFile.exists()) {
                InputStream inputStream = new FileInputStream(backupFile);

                StreamingResponseBody responseBody = outputStream -> {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                };

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backupFile.getName());
                headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

                // Agenda a  do arquivo após 5 minutos (300000 milissegundos)
                dbInfo.FileDeletion(backupFile);

                return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
