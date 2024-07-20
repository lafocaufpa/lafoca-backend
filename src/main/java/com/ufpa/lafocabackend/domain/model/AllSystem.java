package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AllSystem {

    @Id
    private Long id;

    // Backend dinâmico
    private String backendHostAddress;
    private String backendJavaVersion;
    private long backendJvmTotalMemoryMB;
    private long backendJvmFreeMemoryMB;
    private long backendJvmMaxMemoryMB;
    private String backendSpringBootVersion;
    private String backendSpringVersion;
    private String backendSpringSecurityCoreVersion;

    // Backend manual
    private String backendHostName;
    private String backendRamMemory;
    private String backendStorage;
    private String backendProcessorInfo;
    private String backendOsDistributor;
    private String backendOsDescription;
    private String backendOsRelease;
    private String backendOsCodename;

    // Frontend dinâmico
    private String frontendNodeVersion;
    private String frontendNpmVersion;
    private String frontendReactVersion;
    private String frontendNextjsVersion;
    private String frontendNextauthVersion;

    // Frontend manual
    private String frontendHostAddress;
    private String frontendHostName;
    private String frontendRamMemory;
    private String frontendStorage;
    private String frontendProcessorInfo;
    private String frontendOsDistributor;
    private String frontendOsDescription;
    private String frontendOsRelease;
    private String frontendOsCodename;

    // Banco de dados
    private String dbName;
    private String dbVersion;
    private String dbStatus;
    private String dbLastBackup;

    public AllSystem() {
        initializeJvmMemoryInfo();
    }

    private void initializeJvmMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        this.backendJvmTotalMemoryMB = bytesToMB(runtime.totalMemory());
        this.backendJvmFreeMemoryMB = bytesToMB(runtime.freeMemory());
        this.backendJvmMaxMemoryMB = bytesToMB(runtime.maxMemory());
    }

    private long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}