package com.karthik.urlshortener.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class SafeBrowsingRequest {

    private ClientInfo client;
    private ThreatInfo threatInfo;
}
