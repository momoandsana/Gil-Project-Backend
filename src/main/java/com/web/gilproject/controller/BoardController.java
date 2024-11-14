package com.web.gilproject.controller;

import com.web.gilproject.dto.BoardDTO.BoardPathDTO;
import com.web.gilproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class BoardController {
//    private final PathService pathService;
    private final BoardService boardService;


    /*
    나중에 토큰에서 사용자 정보 꺼내서 해당 사용자의 경로 가지고 오기
    임시로 사용자 아이디 받아서 해당 아이디 사용자의 경로들 가지고 오기
     */
    @GetMapping("/{userId}/paths")
    public ResponseEntity<List<BoardPathDTO>> getAllPaths(@PathVariable Long userId)
    {
        List<BoardPathDTO> boardPathListDTO=boardService.getAllPathsById(userId);
        return ResponseEntity.ok(boardPathListDTO);
    }

//    @PostMapping
//    public ResponseEntity<Post> createPost(@RequestBody Post post) {
//        Post createdPost=boardService.createPost(post);
//        return ResponseEntity.ok(createdPost);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Post>> getAllPosts(){
//        List<Post> posts=boardService.getAllPosts();
//        return ResponseEntity.ok(posts);
//    }
//
//    @GetMapping
//    public ResponseEntity<Post> getPostById(@RequestParam Long postId){
//        return
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<Post> updatePost(@PathVariable Long id,@RequestBody Post post){
//
//    }


    // 게시글 삭제

    // 경로별





}
