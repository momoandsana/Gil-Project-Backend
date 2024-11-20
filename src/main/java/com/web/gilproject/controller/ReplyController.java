package com.web.gilproject.controller;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.ReplyDTO.ReplyDTO;
import com.web.gilproject.dto.ReplyDTO.ReplyPostRequestDTO;
import com.web.gilproject.service.BoardService;
import com.web.gilproject.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;
    private final BoardService boardService;

    /*
    해당 게시글의 댓글 목록 가지고 오기
     */
    @GetMapping
    public ResponseEntity<List<ReplyDTO>> getRepliesByPostId(@PathVariable Long postId) {
        List<ReplyDTO> replies=replyService.getRepliesByPostId(postId);
        return ResponseEntity.ok(replies);
    }

    /*
    댓글 달기
     */
    @PostMapping
    public ResponseEntity<ReplyDTO>createReply(
            @PathVariable("postId") Long postId,
            @RequestBody ReplyPostRequestDTO replyPostRequestDTO,
            Authentication authentication,
            SessionStatus sessionStatus)
    {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId=customUserDetails.getId();
        ReplyDTO replyDTO=replyService.createReply(postId,replyPostRequestDTO,userId);
        return ResponseEntity.ok(replyDTO);
    }

    /*
    댓글 수정
     */

    /*
    댓글 삭제
     */

    /*
    댓글 좋아요 기능
     */
}
