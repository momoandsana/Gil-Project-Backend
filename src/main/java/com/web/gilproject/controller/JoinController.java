package com.web.gilproject.controller;

import com.web.gilproject.dto.UserDTO_JHW;
import com.web.gilproject.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String joinProcess(UserDTO_JHW userDTOJHW) {
        joinService.joinProcess(userDTOJHW);

        return "ok";
    }
}
