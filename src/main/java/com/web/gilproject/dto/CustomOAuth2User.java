package com.web.gilproject.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User extends IntergrateUserDetails implements OAuth2User {

    private final UserDTO userDto;

    @Override
    public Map<String, Object> getAttributes() {
        //각 회사마다 데이터가 달라서 사용하기 어려움
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return userDto.getName();
    }

    public String getEmail() {return userDto.getEmail();}

    public String getProfileUrl(){return userDto.getImageUrl();}

    public String getNickname() {return userDto.getNickName(); }

    public Long getId() {return userDto.getId(); }
}
