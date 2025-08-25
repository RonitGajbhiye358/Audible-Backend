// Marks this class as a configuration class for Spring Boot
package com.audible.APIGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsGlobalConfig {

    // Declares a Spring Bean that defines global CORS settings for the API Gateway
    @Bean
    public CorsWebFilter corsWebFilter() {
        // Creates a new CORS configuration object
        CorsConfiguration config = new CorsConfiguration();
        // Allows cookies and credentials to be included in requests
        config.setAllowCredentials(true);
        // Specifies the allowed origin for CORS requests (frontend running at this address)
        config.addAllowedOrigin("http://localhost:5173"); // your frontend origin
        // Allows all headers in CORS requests
        config.addAllowedHeader("*");
        // Allows all HTTP methods (GET, POST, PUT, DELETE, etc.) in CORS requests
        config.addAllowedMethod("*");

        // Maps the CORS configuration to all routes in the gateway
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Returns a new CorsWebFilter with the defined configuration
        return new CorsWebFilter(source);
    }
}
