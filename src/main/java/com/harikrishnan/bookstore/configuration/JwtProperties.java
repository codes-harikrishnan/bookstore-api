package com.harikrishnan.bookstore.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
public class JwtProperties {
    private Long expiration;
    private String secret;
}
