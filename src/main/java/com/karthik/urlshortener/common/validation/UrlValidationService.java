package com.karthik.urlshortener.common.validation;

import com.karthik.urlshortener.common.exception.InvalidUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class UrlValidationService {
    public void validate(String originalUrl) {
        URI uri;
        try {
            uri = new URI(originalUrl);
        } catch (URISyntaxException ex) {
            throw new InvalidUrlException("Invalid URL format");
        }
        validateScheme(uri);
        validateHost(uri);
        validateIpAddress(uri);
    }

    private void validateScheme(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new InvalidUrlException("URL scheme is required");
        }
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            throw new InvalidUrlException("Only HTTP and HTTPS URLs are allowed");
        }
    }

    private void validateHost(URI uri) {
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new InvalidUrlException("Invalid host");
        }

        if (host.equalsIgnoreCase("localhost")) {
            throw new InvalidUrlException("localhost is not allowed");
        }
    }

    private void validateIpAddress(URI uri) {
        try {
            InetAddress address = InetAddress.getByName(uri.getHost());
            if (address.isLoopbackAddress()) {
                throw new InvalidUrlException("Loopback addresses are not allowed");
            }

            if (address.isSiteLocalAddress()) {
                throw new InvalidUrlException("Private IP addresses are not allowed");
            }

            if (address.isAnyLocalAddress()) {
                throw new InvalidUrlException("Local addresses are not allowed");
            }

            if (address.isLinkLocalAddress()) {
                throw new InvalidUrlException("Link-local addresses are not allowed");
            }

        } catch (Exception ex) {
            throw new InvalidUrlException("Unable to resolve host");
        }
    }
}