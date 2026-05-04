package com.sossbar.oauth2.jwt;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Getter
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY= "auth";
    private final UserRepository userRepository;

    @Value("${jwt.expire-time}")
    private String accesstokenExpireTime;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${refresh-token.expire.time}")
    private String refreshTokenExpireTime;

    private SecretKey key;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String generateToken(User user) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + Long.parseLong(accesstokenExpireTime));

        return Jwts.builder()
                .subject(user.getId().toString())   // 토큰 주체를 id로 설정
                .claim(AUTHORITIES_KEY, user.getUserType().toString())
                .issuedAt(now)  // 발행 시간
                .expiration(expireDate) // 만료 시간
                .signWith(key, Jwts.SIG.HS256)  // 토큰 암호화
                .compact(); // 압축, 서명 후 토큰 생성
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;    // 검증 완료 -> 유효한 토큰
            // 검증 실패 시 반환하는 예외에 따라 다르게 실행
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new BusinessException(ErrorCode.JWT_INVALID, "JWT 가 유효하지 않습니다.");
        } catch (SignatureException e) {
            throw new BusinessException(ErrorCode.JWT_SIGNATURE_INVALID, "JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.JWT_EXPIRED, "JWT 가 만료되었습니다.");
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.JWT_EMPTY, "JWT 가 null 이거나 비어있거나 공백만 있습니다.");
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.JWT_INVALID, "JWT 검증에 실패했습니다.");
        }
    }

    // 인증 객체 반환
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Long userId = Long.parseLong(claims.getSubject());
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage())
        );

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(user.getUserType().toString())
        );

        return new UsernamePasswordAuthenticationToken(user.getId(), "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try{
            JwtParser parser = Jwts.parser()
                    .verifyWith(key)
                    .build();

            return parser.parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    // refreshtoken 생성
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + Long.parseLong(refreshTokenExpireTime));

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // userId 추출
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }
}
