package com.sossbar.oauth2.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.common.template.ApiResTemplate;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // 모든 요청이 들어올 때 필터가 가로채 인증 로직 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        try {
            if (uri.startsWith("/api/v1/login")
                    || uri.startsWith("/swagger-ui")
                    || uri.startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        Authentication authentication =
                                jwtTokenProvider.getAuthentication(token);

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                } catch (BusinessException e) {

                    if (e.getErrorCode() == ErrorCode.JWT_EXPIRED) {
                        setErrorResponse(response, ErrorCode.JWT_EXPIRED);
                        return;
                    }

                    setErrorResponse(response, e.getErrorCode());
                    return;
                }
            }
            filterChain.doFilter(request, response);

        } catch (BusinessException ex) {
            setErrorResponse(response, ex.getErrorCode());
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatusCode());
        response.setContentType("application/json;charset=UTF-8");

        ApiResTemplate<?> body = ApiResTemplate.errorResponse(errorCode, errorCode.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    // 요청에서 토큰 추출 메소드 -> 쿠키 추출로 변경
    private String resolveToken(HttpServletRequest request) {
        // 쿠키 추출
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
