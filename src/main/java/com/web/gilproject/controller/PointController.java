package com.web.gilproject.controller;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PointConfirmDTO;
import com.web.gilproject.exception.ErrorCode;
import com.web.gilproject.exception.MemberAuthenticationException;
import com.web.gilproject.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    //따라걷기 끝났을 때 포인트+10,해당 아이디에 대한 따라걷기정보 DB에 저장.
    @PatchMapping("/point")
    public void PointController(@RequestBody Long pathId, Authentication authentication) {
        CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customMemberDetails.getId();
        if(userId == null)
        {
            throw new MemberAuthenticationException(ErrorCode.NOTFOUND_USER);
        }

        pointService.pointPlus(userId,pathId);

    }

    //현재 로그인된 유저 포인트 뿌려주기
    @GetMapping("/point")
    public ResponseEntity<Integer> PointController(Authentication authentication) {
        CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customMemberDetails.getId();
        if(userId == null)
        {
            throw new MemberAuthenticationException(ErrorCode.NOTFOUND_USER);
        }

        int point =  pointService.getPoint(userId);

        return ResponseEntity.ok(point);
    }


    //따라걷기 저장
    @GetMapping("/walkAlongs")
    public ResponseEntity<?> getWalkAlongLength(Authentication authentication) {
        CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customMemberDetails.getId();
        if(userId == null)
        {
            throw new MemberAuthenticationException(ErrorCode.NOTFOUND_USER);
        }
        int walkAlongSize= pointService.getWalkAlongLength(userId);

        return ResponseEntity.ok(walkAlongSize);
        
    }
}
