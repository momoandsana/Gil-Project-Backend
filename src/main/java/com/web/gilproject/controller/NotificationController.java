package com.web.gilproject.controller;

import com.web.gilproject.dto.IntergrateUserDetails;
import com.web.gilproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /*
    * ★클라이언트의 이벤트 구독을 수락한다.
    * Sse를 연결하기 위해서는 TEXT_EVENT_STREAM_VALUE으로 설정해야한다.
    * TEXT_EVENT_STREAM_VALUE타입으로 설정하면, 서버가 클라이언트에게 이벤트 스트림을 전송한다는것을 명시.
    * 이를통해 서버는 클라이언트와의 연결을 유지하며 실시간으로 데이터를 전송할 수 있다.
    *
    * 클라이언트는 이 타입의 응답을 통해 서버가 이벤트 스트림을 전송할 준비가 되어있음을 인지하고
    * 서버로부터 데이터를 전달받을 수 있다.
    * 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
    * */

    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id값을 의미. 항상 존재하는 것이 아니기 때문에 false
    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            //@Parameter(hidden=true) @AuthenticationPrincipal IntergrateUserDetails intergrateUserDetails,
            @PathVariable Long id,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
        //return ResponseEntity.ok(notificationService.subscribe(intergrateUserDetails.getId(), lastEventId));
        return ResponseEntity.ok(notificationService.subscribe(id, lastEventId));
    }

    /**
     * 이벤트를 구독 중인 클라이언트에게 데이터를 전송한다.
     */
//    @GetMapping("/broadcast/{userId}")
//    public void broadcast(@PathVariable Long userId, @RequestBody EventPayLoad eventPayload){
//        notificationService.broadcast(userId, eventPayload);
//    }

    @PostMapping("/send-data/{id}")
    public void sendData(@PathVariable Long id){
        notificationService.notify(id,"data");
    }
}
