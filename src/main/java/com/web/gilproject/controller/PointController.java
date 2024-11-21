package com.web.gilproject.controller;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PointConfirmDTO;
import com.web.gilproject.exception.ErrorCode;
import com.web.gilproject.exception.MemberAuthenticationException;
import com.web.gilproject.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PatchMapping("/point")
    public void PointController(@RequestBody PointConfirmDTO pointConfirmDTO, Authentication authentication) {
        CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customMemberDetails.getId();
        if(userId == null)
        {
            throw new MemberAuthenticationException(ErrorCode.NOTFOUND_USER);
        }

        if (pointConfirmDTO.getDistance() == null || pointConfirmDTO.getTime() == null) {
            throw new MemberAuthenticationException(ErrorCode.POINTPLUS_FAILED);
        }

        pointService.pointPlus(userId);

    }
}
