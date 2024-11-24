package com.web.gilproject.controller;

import com.web.gilproject.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscribe")
@Slf4j
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping("/{userId}/{subscribeUserId}")
    public ResponseEntity<?> createSubscribe(@PathVariable Long userId, @PathVariable Long subscribeUserId) {
        log.info("UserId = {}, subscribeUserId = {}", userId, subscribeUserId);
        int re = subscribeService.createSubscribe(userId, subscribeUserId);
        return new ResponseEntity<>(re, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/{subscribeUserId}")
    public ResponseEntity<?> deleteSubscribe(@PathVariable Long userId, @PathVariable Long subscribeUserId) {
        log.info("UserId = {}, subscribeUserId = {}", userId, subscribeUserId);
        int re = subscribeService.deleteSubscribe(userId, subscribeUserId);
        return new ResponseEntity<>(re, HttpStatus.OK);
    }

}
