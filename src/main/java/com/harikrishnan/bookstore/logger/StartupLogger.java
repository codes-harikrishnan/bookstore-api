package com.harikrishnan.bookstore.logger;

import com.harikrishnan.bookstore.configuration.JwtProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupLogger {

    private final JwtProperties jwtProperties;

    @PostConstruct
    public void logApplicationStart () {
        log.info("Application started");
        logJwtDetails();

    }

    @PreDestroy
    public void logApplicationEnd () {
        log.info("Application ended");
    }

    public void logJwtDetails () {
        log.info("Jwt expiration time is = {}",jwtProperties.getExpiration());
    }

}
