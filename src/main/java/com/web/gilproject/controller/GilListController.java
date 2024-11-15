package com.web.gilproject.controller;

import com.web.gilproject.domain.Post;
import com.web.gilproject.service.GilListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 게시글 목록 조회를 위한 컨트롤러
 * */
@RestController
@RequestMapping("/gilList")
@RequiredArgsConstructor
public class GilListController {

    private final GilListService gilListService;

    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    @GetMapping("/{nowY}/{nowX}")
    public ResponseEntity<?> findByMyPosition(@PathVariable double nowY, @PathVariable double nowX){
        List<Post> listPost = gilListService.findByMyPosition(nowY, nowX);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }
}
