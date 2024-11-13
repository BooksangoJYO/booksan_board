package io.booksan.booksan_board.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	
	@Value("${booksan.front}")
	private String booksanFront;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf
                .disable()
                )
                .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(matchers -> matchers
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers(
                        "/",
                        "/api/**",
                        "/js/**",
                        "/css/**",
                        "/images/**",
                        //게시판, 책 리뷰(로그인 써야되는 api 요청들)
                        "/api/board/insert",
                        "/api/board/update",
                        "/api/board/delete",
                        "/api/books/comment/insert",
                        "/api/books/comment/update",
                        "/api/books/comment/delete"                        
                ).permitAll()
                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("booksanFront")); // 실제 운영환경에서는 구체적인 도메인 지정 필요
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
