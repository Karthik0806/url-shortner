package com.karthik.urlshortener.security.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum SecurityPrefix {
    ROLE("ROLE_");
    private final String value;
}
