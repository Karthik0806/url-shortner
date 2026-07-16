package com.karthik.urlshortener.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class ThreatInfo {

    private List<String> threatTypes;
    private List<String> platformTypes;
    private List<String> threatEntryTypes;
    private List<ThreatEntry> threatEntries;
}
