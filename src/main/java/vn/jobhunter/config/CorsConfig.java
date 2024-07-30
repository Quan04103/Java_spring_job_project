package vn.jobhunter.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // cho phép các URL nào có thể kết nối tới backend
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000", "http://localhost:4173", "http://localhost:5173"));

        // các method nào được kết nối
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed methods

        // các phần header được phép gửi lên
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                "Accept", "x-no-entry"));

        // gửi kèm cookies hay không
        configuration.setAllowCredentials(true);

        // How long the response from a pre-flight request can be cached by clients
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }
}