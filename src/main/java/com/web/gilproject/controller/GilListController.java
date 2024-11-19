package com.web.gilproject.controller;

import com.web.gilproject.dto.PostDTO_YJ.PostDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.service.GilListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 목록 조회를 위한 컨트롤러
 * */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class GilListController {

    private final GilListService gilListService;

    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    @GetMapping("/{nowY}/{nowX}")
    public ResponseEntity<?> findByMyPosition(@PathVariable Double nowY, @PathVariable Double nowX){
        List<PostResDTO> listPost = gilListService.findByMyPosition(nowY, nowX);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    @GetMapping("/nearAddr")
    public ResponseEntity<?> findByNearAddr(Authentication authentication){
        List<PostResDTO> listPost = gilListService.findByNearAddr(authentication);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    @GetMapping("/nickName")
    public ResponseEntity<?> findByNickName(@RequestParam String nickName){
        List<PostResDTO> listPost = gilListService.findByNickName(nickName);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    @GetMapping("/myGilList")
    public ResponseEntity<?> findMyGilList(Authentication authentication){
        List<PostResDTO> listPost = gilListService.findMyGilList(authentication);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    @GetMapping("/myFav")
    public ResponseEntity<?> findMyFav(Authentication authentication){
        List<PostResDTO> listPost = gilListService.findMyFav(authentication);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    @GetMapping("/keyword")
    public ResponseEntity<?> findByKeyword(@RequestParam String keyword){
        List<PostResDTO> listPost = gilListService.findByKeyword(keyword);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }
}
