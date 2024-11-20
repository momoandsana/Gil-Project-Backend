package com.web.gilproject.controller;

import com.web.gilproject.dto.IntergrateUserDetails;
import com.web.gilproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    * @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId)
    * : Last-Event-ID는 헤더에 담겨져 오는 값으로 이전에 받지 못한 이벤트가 존재하는 경우
    *   (SSE연결에 대한 시간 만료 혹은 종료)나 받은 마지막 이벤트 ID 값을 넘겨
    *   그 이후의 데이터(받지 못한 데이터)부터 받을 수 있게 할때 필요한 값이다.
    *
    * ★★★ 요약 : 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
    * */

    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id값을 의미. 항상 존재하는 것이 아니기 때문에 false
    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @PathVariable Long id,
            //@AuthenticationPrincipal IntergrateUserDetails intergrateUserDetails,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
        log.info("Sse 세션 연결");
        return ResponseEntity.ok(notificationService.subscribe(id, lastEventId));
        //return ResponseEntity.ok(notificationService.subscribe(intergrateUserDetails.getId(), lastEventId));
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
        log.info("이벤트를 구독 중인 클라이언트에게 데이터를 전송한다.");
        notificationService.notify(id,"data");

    }
}
