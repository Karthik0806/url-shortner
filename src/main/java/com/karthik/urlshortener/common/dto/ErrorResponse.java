package com.karthik.urlshortener.common.dto;

import lombok.Builder;

import lombok.Getter;

import java.time.Instant;

@Getter

@Builder

public class ErrorResponse {

    private boolean success;

    private String message;

    private Instant timestamp;

}
