package com.sigma.gym.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {
    
    // WebClient configuration temporarily disabled to avoid startup issues
    // TODO: Re-enable when WebClient dependency is resolved
    
//    @Bean
//    public WebClient.Builder webClientBuilder() {
//        return WebClient.builder()
//                .codecs(configurer -> configurer
//                        .defaultCodecs()
//                        .maxInMemorySize(16 * 1024 * 1024)); // 16MB buffer
//    }
//    
//    @Bean 
//    public WebClient webClient(WebClient.Builder webClientBuilder) {
//        return webClientBuilder.build();
//    }
}
