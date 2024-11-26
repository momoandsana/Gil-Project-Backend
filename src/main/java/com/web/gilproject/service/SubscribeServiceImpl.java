package com.web.gilproject.service;

import com.web.gilproject.domain.Subscribe;
import com.web.gilproject.domain.User;
import com.web.gilproject.repository.SubscribeRepository;
import com.web.gilproject.repository.UserRepository_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final UserRepository_emh userRepository;

    @Transactional
    @Override
    public List<Subscribe> findAllByUserId(Long userId) {
        log.info("userId = " + userId);
        User user = userRepository.findById(userId).orElse(null);
        List<Subscribe> subscriptions = subscribeRepository.findByUserId(user);
        log.info("subscriptions = " + subscriptions);
        return subscriptions;
    }

    @Transactional
    @Override
    public int createSubscribe(Long userId, Long subscribeUserId) {
        User user = userRepository.findById(userId).orElse(null);
        User subUser = userRepository.findById(subscribeUserId).orElse(null);

        Optional<Subscribe> existingSubscribe = subscribeRepository.findByUserIdAndSubscribeUserId(user, subUser);
        if(existingSubscribe.isPresent()) {
            log.info("이미 구독 관계가 존재합니다");
            return 0;
        } else if(userId.equals(subscribeUserId)) return 0;

        Subscribe subscribe = Subscribe.builder().userId(user).subscribeUserId(subUser).build();
        Subscribe dbSubscribe = subscribeRepository.save(subscribe);
        log.info("dbSubscribe: " + dbSubscribe);
        return 1;
    }

    @Transactional
    @Override
    public int deleteSubscribe(Long userId, Long subscribeUserId) {
        User user = userRepository.findById(userId).orElse(null);
        User subUser = userRepository.findById(subscribeUserId).orElse(null);

        Subscribe existingSubscribe = subscribeRepository.findByUserIdAndSubscribeUserId(user, subUser).orElse(null);
        if(existingSubscribe == null) {
            log.info("구독 해지할 UserId가 없습니다.");
            return 0;
        }

        subscribeRepository.delete(existingSubscribe);
        return 1;
    }
}
