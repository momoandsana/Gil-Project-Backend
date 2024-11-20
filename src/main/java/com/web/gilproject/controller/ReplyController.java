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

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;
    private final BoardService boardService;

    /**
     * 해당 게시글의 댓글 목록 가지고 오기
     * @param postId
     * @return
     */
    @GetMapping
    public ResponseEntity<List<ReplyDTO>> getRepliesByPostId(@PathVariable Long postId) {
        List<ReplyDTO> replies=replyService.getRepliesByPostId(postId);
        return ResponseEntity.ok(replies);
    }

    /**
     * 댓글 달기
     * @param postId
     * @param replyPostRequestDTO
     * @param authentication
     * @return
     */
    @PostMapping
    public ResponseEntity<Void>createReply(
            @PathVariable("postId") Long postId,
            @RequestBody ReplyPostRequestDTO replyPostRequestDTO,
            Authentication authentication
            )
    {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId=customUserDetails.getId();
        ReplyDTO replyDTO=replyService.createReply(postId,replyPostRequestDTO,userId);
        return ResponseEntity.ok().build();
    }

    /*
    댓글 수정
     */

    /**
     * 댓글 삭제
     * @param postId
     * @param replyId
     * @param authentication
     * @return
     */
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long postId, @PathVariable Long replyId, Authentication authentication)
    {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId=customUserDetails.getId();
        replyService.deleteReply(postId,replyId,userId);
        return ResponseEntity.ok().build();
    }
    /*
    댓글 좋아요 기능
     */
}
