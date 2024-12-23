package io.booksan.booksan_board.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Component
public class TokenChecker {
    
    @Value("${booksan.users}")
    private String booksanUsers;
    
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> tokenCheck(String accessToken) {
        Map<String, Object> result = new HashMap<>();
        String apiUrl = String.format(
                booksanUsers+"/api/users/checkToken"
        );
        if (accessToken != null) {
            //HttpHeaders 설정
            HttpHeaders putHeaders = new HttpHeaders();
            putHeaders.set("accessToken", accessToken);
            //HttpEntity 객체 생성( 본문은 필요 없으므로 void 사용)
            HttpEntity<String> entity = new HttpEntity<>(putHeaders);

            //API 요청 및 응답 받기
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String email = response.getBody();
            if (email != null && email.length() > 0) {
                result.put("status", true);
                result.put("email", email);
                return result;
            }
        }
        result.put("status", false);
        return result;

    }
}
