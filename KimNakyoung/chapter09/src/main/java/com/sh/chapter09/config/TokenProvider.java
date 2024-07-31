package com.sh.chapter09.config;


import com.sh.chapter09.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    // 토큰 생성
    // 올바른 토큰인지 우효성 검사
    // 토큰에서 필요한 정보를 가져오는 클래스 작성

    private final JwtProperties jwtProperties;


    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }


    // 1. JWT 토큰 생성 메서드
    // expiry : 만료시간 , user : 유저정보
    public String makeToken(Date expiry, User user) {

        Date now = new Date();

        // set 메서드를 통해 여러값 지정함.

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) // iss(발급자) : abocado216@naver.com (yml)
                .setIssuedAt(now) // iat(발급일시) : 현재시간
                .setExpiration(expiry) // 내용 exp(만료일시) : expiry 멤버 변숫값
                .setSubject(user.getEmail())  // 내용 sub(토큰제목) : 유저 이메일
                .claim("id", user.getId()) // 클레임 id : 유저  ID
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 프로퍼티즈 파일에 선언해둔 비밀값 HS256방식으로 암호화
                .compact();
    }

    // 토큰 검증 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    //토큰 기반으로 인증 정보를 가져오는 메서드
    // 토큰을 받아 인증 벙보를 담은 객체 Authentication을 반환 하는 메서드

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); // 클레임정보를 반환받아
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        // 사용자 이메일이 들어 있는 토큰 제목  sub과 토큰 기반으로 인증정보 생성
        // 이때 User은 프로젝트에서 만든거 말고 스프링시큐리티에서 제공하는 User클래스 임포트
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }

    // 토큰 기반으로 유저 ID를 가져오는 메서드

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

}
