package com.karthik.urlshortener.common.security;


import com.karthik.urlshortener.common.config.SafeBrowsingProperties;
import com.karthik.urlshortener.common.dto.*;
import com.karthik.urlshortener.common.exception.UnsafeUrlException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SafeBrowsingService {
    private final RestClient restClient;
    private final SafeBrowsingProperties properties;
    public void check(String url) {
        log.info("Checking URL with Google Safe Browsing: {}", url);
        SafeBrowsingRequest request = SafeBrowsingRequest.builder()
                        .client(new ClientInfo("knurls","1.0"))
                        .threatInfo(ThreatInfo.builder()
                                        .threatTypes(List.of(
                                                        "MALWARE",
                                                        "SOCIAL_ENGINEERING",
                                                        "UNWANTED_SOFTWARE"))
                                        .platformTypes(List.of("ANY_PLATFORM"))
                                        .threatEntryTypes(List.of("URL"))
                                        .threatEntries(List.of(new ThreatEntry(url)))
                                .build()).build();

        SafeBrowsingResponse response = restClient.post()
                        .uri(properties.getUrl()
                                        + "?key="
                                        + properties.getApiKey())
                        .body(request)
                        .retrieve()
                        .body(SafeBrowsingResponse.class);

        if (response != null && response.getMatches() != null && !response.getMatches().isEmpty()) {
            log.warn("Unsafe URL detected: {}", url);
            throw new UnsafeUrlException("Unsafe URL detected. This destination has been reported as phishing, malware, or unwanted software.");
        }
        log.info("Google Response: {}", response);
        log.info("Safe Browsing passed: {}", url);
    }
}