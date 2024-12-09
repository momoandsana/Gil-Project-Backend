package com.web.gilproject.controller;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscribe")
@Slf4j
public class SubscribeController {

    private final SubscribeService subscribeService;

    //구독 추가
    @PostMapping("/{subscribeUserId}")
    public ResponseEntity<?> createSubscribe(Authentication authentication, @PathVariable Long subscribeUserId) {
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("UserId = {}, subscribeUserId = {}", userId, subscribeUserId);
        int re = subscribeService.createSubscribe(userId, subscribeUserId);
        return new ResponseEntity<>(re, HttpStatus.OK);
    }

    //구독 해지
    @DeleteMapping("/{subscribeUserId}")
    public ResponseEntity<?> deleteSubscribe(Authentication authentication, @PathVariable Long subscribeUserId) {
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("UserId = {}, subscribeUserId = {}", userId, subscribeUserId);
        int re = subscribeService.deleteSubscribe(userId, subscribeUserId);
        return new ResponseEntity<>(re, HttpStatus.OK);
    }

}
