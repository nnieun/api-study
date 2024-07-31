package com.sh.chapter09.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {

    //issuer 필드에 yml에서 설정한 jwt.issuer 값이 secretKey에는 jwt.secret_key값이 매핑

    private String issuer;
    private String secretKey;
}
