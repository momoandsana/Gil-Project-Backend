package com.web.gilproject.controller;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.service.GilListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> findByMyPosition(@PathVariable Double nowY, @PathVariable Double nowX, Integer page, Integer size, Authentication authentication) {
        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        Page<PostResDTO> listPost = gilListService.findByMyPosition(nowY, nowX, PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    @GetMapping("/nearAddr")
    public ResponseEntity<?> findByNearAddr(Authentication authentication, Integer page, Integer size) {
        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findByNearAddr(authentication, PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    @GetMapping("/nickName")
    public ResponseEntity<?> findByNickName(@RequestParam String nickName, Integer page, Integer size, Authentication authentication) {

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findByNickName(nickName, PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    @GetMapping("/myGilList")
    public ResponseEntity<?> findMyGilList(Authentication authentication, Integer page, Integer size){

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findMyGilList(authentication, PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    @GetMapping("/myFav")
    public ResponseEntity<?> findMyFav(Authentication authentication, Integer page, Integer size){

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findMyFav(authentication, PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    @GetMapping("/keyword")
    public ResponseEntity<?> findByKeyword(@RequestParam String keyword, Integer page, Integer size, Authentication authentication) {

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findByKeyword(keyword, PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }
}
