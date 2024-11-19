package com.web.gilproject.controller;

import com.web.gilproject.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/{postId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    /*
    해당 게시글의 댓글 목록 가지고 오기
     */

    /*
    댓글 달기
     */

    /*
    댓글 수정
     */

    /*
    댓글 삭제
     */
}
