package com.matdang._01_oauth.auth.service;

import com.matdang._01_oauth.auth.dto.CustomOAuth2User;
import com.matdang._01_oauth.auth.dto.NaverResponse;
import com.matdang._01_oauth.auth.dto.OAuth2Response;
import com.matdang._01_oauth.auth.entity.UserEntity;
import com.matdang._01_oauth.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //부모 클래스 loadUser로 부터 유저 정보를 가지고 오는 메서드 ( OAuth2 공급업체로 부터 사용자 정보를 가져오는 것 )
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("CustomOAuth2UserService#loadUser oAuth2User = " + oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("registrationId = {}", registrationId);

        OAuth2Response oAuth2Response =null;


        if(registrationId.equals("naver")){
            log.debug("naver");
            oAuth2Response = new NaverResponse((Map)oAuth2User.getAttributes().get("response"));
            log.debug("NaverResponse = {}", oAuth2Response.getName());
        }
        else if (registrationId.equals("google")){
            log.debug("google");
//                oAuth2Response =
//                        new GoogleResponse(oAuth2User.getAttribute());
        }else{
            log.debug("failed");
            return null;
        }

// 구글과 네이버 서비스마다 인증 규격이 상이하기 때문에 서로 다른 DTO로 담아야 한다.
// 따라서 OAuth2 DTO 객체 격인 OAuth2Response 객체를 인터페이스로 만든다.
// 네이버로 인터페이스를 구현, 구글 타입으로 인터페이스를 구현하는 식으로 진행한다.
//        String username = oAuth2Response.getProvider()+ " "+oAuth2Response.getProviderId();
        String username = oAuth2Response.getProviderId();

        UserEntity existData = userRepository.findByUsername(username);

        String role = null;

        if(existData == null){
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");
            userEntity.setEmail(oAuth2Response.getEmail());
            userRepository.save(userEntity);
        }else{
            log.debug("exist data");
            role = existData.getRole();
            existData.setEmail(oAuth2Response.getEmail());
            userRepository.save(existData);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}

