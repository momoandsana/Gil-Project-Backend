package com.web.gilproject.controller;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.exception.GilListErrorCode;
import com.web.gilproject.exception.GilListException;
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

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }
    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    @GetMapping("/nearAddr")
    public ResponseEntity<?> findByNearAddr(Integer page, Integer size, Authentication authentication) {
        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findByNearAddr(PageRequest.of(page, size), userId);

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

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

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    @GetMapping("/myGilList")
    public ResponseEntity<?> findMyGilList(Integer page, Integer size, Authentication authentication){

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findMyGilList(PageRequest.of(page, size), userId);

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    @GetMapping("/myFav")
    public ResponseEntity<?> findMyFav(Integer page, Integer size, Authentication authentication){

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findMyFav(PageRequest.of(page, size), userId);

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

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

        //검색어가 공백일 경우 예외 처리
        if(keyword.trim().equals("")){
            throw new GilListException(GilListErrorCode.EMPTY_INPUT);
        }

        //검색어에 이모지가 있을 경우 예외 처리
        //이모지 패턴
        String emojiPattern = "[\\uD83C\\uDF00-\\uD83D\\uDDFF" +  // 서플리먼트 영역(자연, 동물, 음식 등의 이모지)
                "\\uD83E\\uDD00-\\uD83E\\uDDFF" +  // 서플리먼트 심볼(새로운 유니코드 표준에 추가된 이모지)
                "\\u2600-\\u26FF" +               // 기본 다국어 기호(이모지가 아니라 기호로 시작했지만, 현대에는 이모지로 사용되는 것들)
                "\\u2700-\\u27BF" +               // 딩벳(문자의 꾸밈을 위한 장식 기호)
                "]+";

        if(keyword.matches(".*"+emojiPattern+".*")){
            throw new GilListException(GilListErrorCode.EMOJI_INPUT);
        }

        //검색어 길이 제한(최소 2자, 최대 30자) 예외 처리
        if(keyword.length()<2 || keyword.length()>30){
            throw new GilListException(GilListErrorCode.WRONG_LENGTH);
        }

        Page<PostResDTO> listPost = gilListService.findByKeyword(keyword, PageRequest.of(page, size), userId);

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 7. 태그 검색으로 글목록 조회하기
     * */
    @GetMapping("/tag")
    public ResponseEntity<?> findByTag(@RequestParam String tag, Integer page, Integer size, Authentication authentication) {
        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findByTag(tag, PageRequest.of(page, size), userId);

        //검색 결과가 없을 때 예외 처리
        if (listPost.getTotalElements() == 0) {
            throw new GilListException(GilListErrorCode.NOTFOUND_LIST);
        }

        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }
}
