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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository_JHW userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println("저장된 유저 " + oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        //어디서 온 Oauth 요청인지 확인(각자 주는 데이터 형식이 다르기 때문)
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        } else {
            return null;
        }

        //DB에서 이메일로 조회
        User user = userRepository.findUserWithPlatformNotZero(oAuth2Response.getEmail());

        if (user == null) {
            User userEntity = new User();

            String nickName = ""; //랜덤한 닉네임 부여
            String uuid = UUID.randomUUID().toString().substring(0, 8);

            //플랫폼 설정, 닉네임 설정
            switch (registrationId) {
                case "naver":
                    userEntity.setPlatform(2);
                    nickName = "N";
                    break;
                case "google":
                    userEntity.setPlatform(1);
                    nickName = "G";
                    break;
                case "kakao":
                    userEntity.setPlatform(3);
                    nickName = "K";
                    break;
            }
            userEntity.setNickName(nickName + uuid);
            userEntity.setName(oAuth2Response.getName());
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setImageUrl(oAuth2Response.getProfileUrl());

            userRepository.save(userEntity); //DB 저장

        } else {
            System.out.println("OAuth - 이메일이 DB에 존재");
            //이미 있을 경우 내용을 업데이트하는 코드
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setName(oAuth2Response.getName());
        userDTO.setEmail(oAuth2Response.getEmail());
        userDTO.setImageUrl(oAuth2Response.getProfileUrl());
        user = userRepository.findUserWithPlatformNotZero(oAuth2Response.getEmail());
        userDTO.setId(user.getId());
        userDTO.setNickName(user.getNickName());

        return new CustomOAuth2User(userDTO);  //인증된 사용자 정보 확인
    }
}
