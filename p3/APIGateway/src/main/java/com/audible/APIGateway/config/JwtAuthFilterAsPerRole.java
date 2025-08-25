// Defines the package for the API Gateway configuration
package com.audible.APIGateway.config;

// Imports required JWT and Spring Gateway classes
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

@Component // Marks the class as a Spring component to be registered automatically
public class JwtAuthFilterAsPerRole implements GatewayFilter { // Custom Gateway filter that restricts access based on JWT roles

    // Static secret key used for signing/verifying JWT tokens
    private static final String SECRET_KEY = "dd08bd1d7f4a3155d5c8cdb0c644c60df71c95862c15b2bfc7e2b396d63f078c";

    // Decodes the base64-encoded secret key and returns a SecretKey instance
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

    // Extracts the "role" claim from the JWT token
    private String extractRole(String token) {
        Claims claim = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claim.get("role", String.class); // assumes "role" is a string like "ADMIN"
    }

    // Main filtering logic to authenticate and authorize based on user role
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Logs the request path for debugging
        System.out.println("JWT filter triggered for path: " + exchange.getRequest().getPath());

        // Extracts the Authorization header from the request
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Returns 401 Unauthorized if the header is missing or malformed
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Removes the "Bearer " prefix from the token
        String token = authHeader.substring(7);
        try {
            // Parses the JWT to extract username and role
            String username = extractUsername(token);
            String role = extractRole(token);

            // Logs username and role for debugging
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);

            // Gets the current request path
            String path = exchange.getRequest().getPath().toString();

            // ✅ Blocks access to /admin/** if role is not ADMIN
            if (path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            } 
            // ✅ Blocks access to /user/** if role is neither USER nor ADMIN
            else if (path.startsWith("/user/") && !"USER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
                // If you want admins to also access /user/**, keep the second condition
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // 👇 Mutates the request to add username and role as custom headers
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Username", username)
                    .header("X-Role", role)
                    .build();

            // Continues with the modified request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            // Returns 401 if token parsing or validation fails
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
