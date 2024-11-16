package io.booksan.booksan_board.config.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.booksan.booksan_board.config.auth.PrincipalDetailsService;
import io.booksan.booksan_board.util.TokenChecker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenFilter extends OncePerRequestFilter {

    private final TokenChecker tokenChecker;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String path = request.getRequestURI();
        log.info("***필터실행***" + path);
        if (!path.startsWith("/api/")
                || (!path.equals("/api/board/insert") && !path.equals("/api/board/update")
                && !path.startsWith("/api/board/delete") && !path.equals("/api/books/comment/insert")
                && !path.equals("/api/books/comment/update") && !path.startsWith("/api/books/comment/delete")
                && !path.startsWith("/api/board/favorite") && !path.startsWith("/api/books/favorite"))) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.info("필터 통과 실패");
            String token = request.getHeader("accessToken");
            log.info("*****token token " + token);
            if (token != null) {
                // Access Token 검증
                Map<String, Object> result = tokenChecker.tokenCheck(token);
                if ((Boolean) result.get("status")) {
                    UserDetails userDetails = principalDetailsService.loadUserByUsername((String) result.get("email"));
                    // userDetails 객체를 사용하여 인증객체로 생성한다  
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    // 스프링 시큐리티에 인증객체를 설정한다 
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");

        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing token");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", false);
        errorResponse.put("message", message);

        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
