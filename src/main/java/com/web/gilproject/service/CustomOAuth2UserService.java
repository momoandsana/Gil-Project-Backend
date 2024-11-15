package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.*;
import com.web.gilproject.repository.UserRepository_JHW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository_JHW userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        //어디서 온 Oauth 요청인지 확인(각자 주는 데이터 형식이 다르기 때문)
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("kakao")) {

        } else {
            return null;
        }

        User existData = userRepository.findUserWithPlatformNotZero(oAuth2Response.getEmail());

        if (existData == null) {
            User userEntity = new User();

            //플랫폼 설정
            switch (registrationId) {
                case "naver":
                    userEntity.setPlatform(2);
                    break;
                case "google":
                    userEntity.setPlatform(1);
                    break;
                case "kakao":
                    userEntity.setPlatform(3);
                    break;
            }
//            userEntity.setPoint(0);
//            userEntity.setState(0);
//            userEntity.setImageUrl("");
//            userEntity.setImageUrl("");
//            userEntity

            userEntity.setName(oAuth2Response.getName());
            userEntity.setEmail(oAuth2Response.getEmail());
            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setName(oAuth2Response.getName());
            userDTO.setEmail(oAuth2Response.getEmail());
            return new CustomOAuth2User(userDTO);

        } else {
            //이미 있을 경우 업데이트하는 코드
        }

        return null;
    }
}
