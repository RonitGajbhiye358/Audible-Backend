// Defines the package for the API Gateway configuration
package com.audible.APIGateway.config;

// Imports necessary classes for JWT handling, HTTP requests, and Spring Cloud Gateway
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component // Marks this class as a Spring component so it can be discovered and injected where needed
public class JwtAuthFilter implements GatewayFilter { // Implements GatewayFilter to define a custom filter for the API Gateway

    // Static secret key used for signing and verifying JWTs
    private static final String SECRET_KEY = "dd08bd1d7f4a3155d5c8cdb0c644c60df71c95862c15b2bfc7e2b396d63f078c";

    // Decodes the secret key and returns a SecretKey object for signing/verifying tokens
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    // Extracts the username (subject) from the JWT
    private String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Extracts the "role" claim from the JWT
    private String extractRoles(String token) {
        Claims claim = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claim.get("role", String.class);
    }

    // The main filtering logic applied to each request passing through the gateway
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Logs the path for which the filter is triggered
        System.out.println("JWT filter triggered for path: " + exchange.getRequest().getPath());

        // Retrieves the Authorization header from the incoming request
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Checks if the Authorization header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Returns a 401 Unauthorized response if the token is invalid or absent
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extracts the JWT token from the header by removing the "Bearer " prefix
        String token = authHeader.substring(7);
        try {
            // Parses the token to extract the username and roles
            String username = extractUsername(token);
            String roles = extractRoles(token);
            System.out.println(username);
            System.out.println(roles);

            // Mutates the request to add custom headers for username and role
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Username", username)
                    .header("X-Role", roles)
                    .build();

            // Continues the filter chain with the modified request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            // If an exception occurs during token parsing, return a 401 Unauthorized response
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
