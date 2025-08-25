package com.audible.APIGateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

	@Autowired
    private JwtAuthFilterAsPerRole jwtAuthFilterAsPerRole;
	
	@Autowired
	private JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Protected route with JWT filter
        		.route("AUDIOBOOK-SERVICE", r -> r.path("/audiobooks/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://AUDIOBOOK-SERVICE"))
        		.route("BOOKCART-SERVICE", r -> r.path("/cart/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://BOOKCART-SERVICE"))
        		.route("BOOKCART-SERVICE", r -> r.path("/orders/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://BOOKCART-SERVICE"))
        		.route("PAYMENT-SERVICE", r -> r.path("/payments/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://PAYMENT-SERVICE"))
        		.route("AUTH-LOGIN-REGISTER", r -> r.path("/auth/login", "/auth/register")
        	            .uri("lb://AUTH-SERVICE")) // 👈 No filter here
        		.route("AUTH-SERVICE", r -> r.path("/user/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://AUTH-SERVICE"))
        		.route("AUTH-SERVICE", r -> r.path("/auth/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://AUTH-SERVICE"))
        		.route("AUTH-SERVICE", r -> r.path("/admin/**")
        		        .filters(f -> f.filter(jwtAuthFilterAsPerRole)) // 👈 MUST be here
        		        .uri("lb://AUTH-SERVICE"))
                .build();
    }
}
