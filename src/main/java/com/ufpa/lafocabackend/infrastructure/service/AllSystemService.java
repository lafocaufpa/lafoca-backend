package com.ufpa.lafocabackend.infrastructure.service;

import com.ufpa.lafocabackend.core.database.DbInfo;
import com.ufpa.lafocabackend.domain.model.AllSystem;
import com.ufpa.lafocabackend.repository.AllSystemRepository;
import org.joda.time.LocalDateTime;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Optional;

@Service
public class AllSystemService {

    private final AllSystemRepository allSystemRepository;
    private final DbInfo dbInfo;

    public AllSystemService(AllSystemRepository allSystemRepository, DbInfo dbInfo) {
        this.allSystemRepository = allSystemRepository;
        this.dbInfo = dbInfo;
    }

    public AllSystem getSystemInfo() {
        Optional<AllSystem> allSystemOptional = allSystemRepository.findById(1L);

        return allSystemOptional.orElseGet(() -> {
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
            newSystem.setDbLastBackup(null);
            newSystem.setId(1L);
            return allSystemRepository.save(newSystem);
        });
    }

    public AllSystem updateSystemInfo(AllSystem manualInfo) {
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
        allSystem.setDbLastBackup(null);
        allSystem.setId(1L);
        allSystemRepository.save(allSystem);

        return allSystem;
    }

    public void updateLastBackup() {
        Optional<AllSystem> allSystemOptional = allSystemRepository.findById(1L);
        if (allSystemOptional.isPresent()) {
            AllSystem allSystem = allSystemOptional.get();
            allSystem.setDbLastBackup(new LocalDateTime().toString());
            allSystemRepository.save(allSystem);
        }
    }
}