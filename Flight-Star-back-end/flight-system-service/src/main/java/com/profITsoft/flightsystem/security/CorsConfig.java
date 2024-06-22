package com.profITsoft.flightsystem.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * This class is used to configure the CORS policy for the application.
 * The allowed origins, methods, headers and exposed headers are configured in the application.properties file.
 * The configuration is then used to create a CorsConfigurationSource bean which is used by the application to
 * enforce the CORS policy.
 */
@Configuration
public class CorsConfig {

    /**
     * The allowed origins are configured in the application.properties file.
     */
    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    /**
     * The allowed methods are configured in the application.properties file.
     */
    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    /**
     * The allowed headers are configured in the application.properties file.
     */
    @Value("#{'${cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    /**
     * The exposed headers are configured in the application.properties file.
     */
    @Value("#{'${cors.exposed-headers}'.split(',')}")
    private List<String> expectedHeaders;

    /**
     * This method is used to create a CorsConfigurationSource bean which is used by the application to enforce the CORS policy.
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(expectedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
