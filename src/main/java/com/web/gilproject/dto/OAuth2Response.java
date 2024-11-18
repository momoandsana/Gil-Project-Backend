package com.web.gilproject.dto;

public interface OAuth2Response {

    /**
     * 제공자 naver,google,kakao 등
     * @return
     */
    String getProvider();

    /**
     * 제공자에서 발급해주는 아이디(번호)
     * @return
     */
    String getProviderId();

    /**
     * 이름(실명이 아닐 수도 있음)
     * @return
     */
    String getName();

    /**
     * 이메일
     * @return
     */
    String getEmail();

    /**
     * 프로필 사진 주소
     * @return
     */
    String getProfileUrl();
}
