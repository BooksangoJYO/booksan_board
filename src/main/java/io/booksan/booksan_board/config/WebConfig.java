package io.booksan.booksan_board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration은 이클래스가 Spring의 설정 클래스로 동작함을 나타냅니다.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //addCorsMappings는 CORS 설정을 등록, CorsRegistry 객체를 사용하여 특정 요청에 대해 CORS 설정 지정
        // /**는 모든 엔드포인트에 대해 CORS를 허용하도록 설정(서버의 모든 URL에 대해 적용됨)
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") //프론트엔드 주소(localhost:5173에서 오는 요청만 허용)
                .allowedMethods("GET", "POST", "PUT", "DELETE"); //허용되는 HTTP 메서드를 지정합니다.
    }
}
