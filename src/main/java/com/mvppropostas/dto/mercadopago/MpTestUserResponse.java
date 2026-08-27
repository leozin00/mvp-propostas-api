package com.mvppropostas.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MpTestUserResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("nickname") String nickname,
    @JsonProperty("password") String password,
    @JsonProperty("profile") String profile) {}
