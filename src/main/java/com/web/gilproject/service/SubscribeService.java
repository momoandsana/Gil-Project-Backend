package com.web.gilproject.service;

import com.web.gilproject.domain.Subscribe;

import java.util.List;

public interface SubscribeService {

    /**
     * 구독 추가
     * @param userId
     * @param subscribeUserId
     * @return
     */
    int createSubscribe(Long userId, Long subscribeUserId);

    /**
     * 구독 해지
     * @param userId
     * @param subscribeUserId
     * @return
     */
    int deleteSubscribe(Long userId, Long subscribeUserId);

    /**
     * 나를 구독하고 있는 유저 조회
     * @param userId
     * @return
     */
    List<Subscribe> findSubscribeByUserId(Long userId);

    /**
     * 내가 구독하고 있는 유저 조회
     * @param userId
     * @return
     */
    List<Subscribe> findMySubscribeByUserId(Long userId);
}
