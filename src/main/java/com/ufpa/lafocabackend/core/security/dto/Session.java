package com.ufpa.lafocabackend.core.security.dto;

import lombok.Builder;

@Builder
public record Session(String token, String user_id, String full_name, String user_email, String issuer, String create_at, String exp_at, String authorities) {}
