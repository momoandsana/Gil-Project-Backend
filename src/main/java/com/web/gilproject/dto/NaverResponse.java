package com.web.gilproject.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        //System.out.println("naver에서 보내는 데이터 " + attribute.toString());
        this.attribute = (Map<String, Object>) attribute.get("response");

    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() { return attribute.get("id").toString();  }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getEmail() {return attribute.get("email").toString();}

    @Override
    public String getProfileUrl() { return attribute.get("profile_image").toString(); }
}
