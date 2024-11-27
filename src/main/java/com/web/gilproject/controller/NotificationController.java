package com.web.gilproject.controller;

import com.web.gilproject.domain.Notification;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.IntergrateUserDetails;
import com.web.gilproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /*
    * ★클라이언트의 이벤트 구독을 수락한다.
    * Sse를 연결하기 위해서는 TEXT_EVENT_STREAM_VALUE으로 설정해야한다.
    * TEXT_EVENT_STREAM_VALUE타입으로 설정하면, 서버가 클라이언트에게 이벤트 스트림을 전송한다는것을 명시.
    * 이를통해 서버는 클라이언트와의 연결을 유지하며 실시간으로 데이터를 전송할 수 있다.
    *
    * produces = MediaType.TEXT_EVENT_STREAM_VALUE
    * : 응답의 Context-Type을 text/event-stream으로 설정
    * : 이 MIME타입은 SSE응답임을 나타내며, 브라우저나 클라이언트는 이 타입을 보고
    *   서버에서 지속적으로 데이터(이벤트 스트림)를 보낼 수 있음을 알게된다.
     *
    * ★★★ 요약 : 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
    * */


    // 1. 연결
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            //Authentication authentication
            @PathVariable Long userId
             ){
        log.info("Sse 세션 연결");
        //CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        //Long userId = customUserDetails.getId();
        SseEmitter emitter = notificationService.subscribe(userId);
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }


    //테스트용(댓글알림)
    @PostMapping("/send-post/{postId}")
    public void sendPost(@PathVariable Long postId){
        log.info("클라이언트에게 글알림");
        notificationService.notifyComment(postId); //글 작성자에게 댓글 알림

    }


    //알림 읽음 처리
    @PostMapping("/read")
    public ResponseEntity<?> markNotificationAsRead(Authentication authentication, Long NotificationId){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        notificationService.markNotificationAsRead(userId, NotificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    //알림 삭제?

}
