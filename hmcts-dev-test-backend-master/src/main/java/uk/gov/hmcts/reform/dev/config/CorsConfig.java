package uk.gov.hmcts.reform.dev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all paths under /api/
            .allowedOrigins("http://localhost:3000") // Specify allowed origins
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE") // Specify allowed HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // If you need to handle cookies or authorization headers
            .maxAge(3600); // Cache duration for preflight requests
    }
}
