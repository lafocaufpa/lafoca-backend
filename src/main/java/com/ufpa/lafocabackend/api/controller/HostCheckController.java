package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.dto.HostInfo;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class HostCheckController {

    public HostCheckController() {
    }

    @GetMapping("/host-check")
    public HostInfo checkHost() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        HostInfo hostInfo = new HostInfo();

        // Informações de Rede
        hostInfo.setHostAddress(localHost.getHostAddress());
        hostInfo.setHostName(localHost.getHostName());
        hostInfo.setCanonicalHostName(localHost.getCanonicalHostName());

        // Informações do Sistema
        hostInfo.setOsName(System.getProperty("os.name"));
        hostInfo.setOsVersion(System.getProperty("os.version"));
        hostInfo.setOsArch(System.getProperty("os.arch"));
        hostInfo.setJavaVersion(System.getProperty("java.version"));
        hostInfo.setUserDir(System.getProperty("user.dir"));
        hostInfo.setUserHome(System.getProperty("user.home"));
        hostInfo.setUserName(System.getProperty("user.name"));

        // Informações do Spring Boot
        hostInfo.setSpringBootVersion(SpringBootVersion.getVersion());
        hostInfo.setSpringVersion(SpringVersion.getVersion());
        hostInfo.setSpringSecurityCoreVersion(SpringSecurityCoreVersion.getVersion());

        return hostInfo;
    }
}
