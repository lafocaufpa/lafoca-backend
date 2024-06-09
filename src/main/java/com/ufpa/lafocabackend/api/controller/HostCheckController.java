package com.ufpa.lafocabackend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class HostCheckController {

    @GetMapping("/hostCheck")
    public String checkHost() throws UnknownHostException {

        return InetAddress.getLocalHost().getHostAddress()
                + " - " + InetAddress.getLocalHost().getHostName();
    }
}