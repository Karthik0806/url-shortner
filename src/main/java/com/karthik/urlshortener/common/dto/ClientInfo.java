package com.karthik.urlshortener.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientInfo {
    private String clientId;
    private String clientVersion;
}
