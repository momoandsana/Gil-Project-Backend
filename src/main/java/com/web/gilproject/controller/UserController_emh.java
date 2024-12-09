package com.web.gilproject.controller;

import com.web.gilproject.domain.Subscribe;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.dto.UserSimpleResDTO;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.GilListService;
import com.web.gilproject.service.PathService;
import com.web.gilproject.service.UserService_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController_emh {

    private final UserService_emh userService;
    private final PathService pathService;
    private final GilListService gilListService;

    /**
     * 내 정보 조회하기 : 마이페이지 누르면 보이는 정보 모두 조회
     * (User정보, 내 산책로 개수, 따라걷기 개수, 따름이 수)
     */
    @GetMapping("/mypage")
    public ResponseEntity<?> findUserById(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("findUserById call...userId = " + userId);
        UserDTO userDTO = userService.findUserById(userId);
        //log.info("userDTO = " + userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * 간단 정보 조회 (프로필 눌렀을 때 다른 유저에게 보이는 내 프로필)
     * (프로필 이미지, 닉네임, 자기소개글, 산ㅊ, 구독자 수, 따라걷기 수)
     */
    @GetMapping("/simpleInfo/{selectedUserId}")
    public ResponseEntity<?> findSimpleInfoById(Authentication authentication, @PathVariable Long selectedUserId){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("findSimpleInfoById call...userId = {}, selectedUserId = {}", userId, selectedUserId);

        UserSimpleResDTO userSimpleResDTO = userService.findSimpleInfoById(userId, selectedUserId);
        //log.info(userSimpleResDTO.toString());
        return new ResponseEntity<>(userSimpleResDTO, HttpStatus.OK);
    }

    /**
     * 내 프로필 이미지 수정
     */
    @PostMapping("/mypage/profile")
    public ResponseEntity<?> updateUserProfile(Authentication authentication, @RequestParam("file") MultipartFile file){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("updateUserProfile call... userId={} file={} ", userId, file);
            userService.updateUserImg(userId, file);
        return ResponseEntity.ok(1);
    }

    /**
     * 자기소개글 수정
     */
    @PutMapping("/mypage/update/comment/{newComment}")
    public ResponseEntity<?> updateUserComment(Authentication authentication, @PathVariable String newComment){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("updateUserComment call.... userId = {}, newComment = {}",userId ,newComment);
        userService.updateUserComment(userId, newComment);
        return ResponseEntity.ok(1);
    }

    /**
     *  비밀번호 변경 (암호화된 정보 받아서 DB에 update)
     */
    @PutMapping("/mypage/update/pwd")
    public ResponseEntity<Integer> updateUserPwd(Authentication authentication, @RequestParam String password, @RequestParam String newPassword){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("updateUserPwd call...userId = {}, password={}", userId, password);

        if(!userService.matchUserPassword(userId, password))
            return ResponseEntity.ok(0);

        userService.updateUserPassword(userId, newPassword);
        return ResponseEntity.ok(1);
    }

    /**
     * 내 주소 수정 (주소API로 집 주소, 집 위도, 집 경도 받아서 내용 수정)
     */
    @PutMapping("/mypage/address")
    public ResponseEntity<Integer> updateUserAddress(Authentication authentication, @RequestBody UserDTO userDTO){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("updateUserAddress call... userId={} userDTO = {}", userId, userDTO);
        userService.updateUserAddr(userId, userDTO);
        return ResponseEntity.ok(1); // 200 OK
    }

    /**
     * 나의 경로 기록 
     */
    @GetMapping("/mypage/myPath")
    public ResponseEntity<?> findPathById(Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("findPathById call..userId={} ", userId);
        List<PathResDTO> pathResDTOList = pathService.findPathByUserIdTransform(userId);
        return new ResponseEntity<>(pathResDTOList, HttpStatus.OK);
    }

     /**
     * 내가 작성한 산책길 글목록
     */
    @GetMapping("/mypage/myPost")
    public ResponseEntity<?> findGilListById(Integer page, Integer size, Authentication authentication){

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        Page<PostResDTO> listPost = gilListService.findMyGilList(PageRequest.of(page, size), userId);
        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }

    /**
     * 내가 찜한 산책길 글목록
     */
    @GetMapping("/mypage/postWishlist")
    public ResponseEntity<?> findPostWishlistById(Integer page, Integer size, Authentication authentication){

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<PostResDTO> listPost = gilListService.findMyFav(PageRequest.of(page, size), userId);

        return new ResponseEntity<>(listPost, HttpStatus.OK);
    }


    /**
     * 내가 구독한 유저
     */
    @GetMapping("mypage/subscribe")
    public ResponseEntity<?> findAllSubscribeByUserId(Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        //log.info("findSubscribeById call...userId = {}", userId);
        List<UserSimpleResDTO> userSimpleResDTOList = userService.findAllSubscribeByUserId(userId);
        return new ResponseEntity<>(userSimpleResDTOList, HttpStatus.OK);
    }

    /**
     *  닉네임 변경
     */
    @PutMapping("/mypage/update/nickname")
    public ResponseEntity<Integer> updateNickName(Authentication authentication, @RequestParam String nickName){
        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Long userId = customUserDetails.getId();
        userService.updateUserNickname(userId, nickName);
        return ResponseEntity.ok(1);
    }

}
