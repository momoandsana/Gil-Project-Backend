package com.web.gilproject.service;

import com.web.gilproject.domain.Subscribe;

import java.util.List;

public interface SubscribeService {

    //구독 조회
    List<Subscribe> findSubscriberByUserId(Long userId);

    //구독 추가
    int createSubscribe(Long userId, Long subscribeUserId);

    //구독 해지
    int deleteSubscribe(Long userId, Long subscribeUserId);

}
