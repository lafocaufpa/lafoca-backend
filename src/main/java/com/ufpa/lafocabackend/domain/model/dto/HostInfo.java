package com.ufpa.lafocabackend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({
        "hostAddress",
        "hostName",
        "canonicalHostName",
        "osName",
        "osVersion",
        "osArch",
        "javaVersion",
        "userDir",
        "userHome",
        "userName",
        "jvmTotalMemoryMB",
        "jvmFreeMemoryMB",
        "jvmMaxMemoryMB"
})
@Setter
@Getter
public class HostInfo {
    // Informações de Rede
    private String hostAddress;
    private String hostName;
    private String canonicalHostName;

    // Informações do Sistema Operacional
    private String osName;       // Nome do sistema operacional
    private String osVersion;    // Versão do sistema operacional
    private String osArch;       // Arquitetura do sistema

    // Informações da JVM (Java Virtual Machine)
    private String javaVersion;  // Versão do Java utilizado pela aplicação

    // Informações do Diretório do Usuário
    private String userDir;      // Diretório atual de trabalho do usuário
    private String userHome;     // Diretório home do usuário no sistema
    private String userName;     // Nome do usuário

    // Informações de Memória da JVM em MB
    private long jvmTotalMemoryMB;    // Memória total disponível para a JVM em MB
    private long jvmFreeMemoryMB;     // Memória atualmente livre na JVM em MB
    private long jvmMaxMemoryMB;      // Limite máximo de memória configurado para a JVM em MB

    // Informações do Spring Boot
    private String springBootVersion; // Versão do Spring Boot
    private String springVersion;
    private String springSecurityCoreVersion;

    public HostInfo() {
        initializeJvmMemoryInfo();
    }

    private void initializeJvmMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        this.jvmTotalMemoryMB = bytesToMB(runtime.totalMemory());
        this.jvmFreeMemoryMB = bytesToMB(runtime.freeMemory());
        this.jvmMaxMemoryMB = bytesToMB(runtime.maxMemory());
    }

    private long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
