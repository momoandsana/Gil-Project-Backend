package com.web.gilproject.controller;

import com.web.gilproject.dto.MailDTO;
import com.web.gilproject.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @GetMapping("/")
    public String mailPage(){
        return "Mail";
    }

    @PostMapping("/send")
    public int mailSend(@RequestBody MailDTO mailDto){
        int code = mailService.sendMail(mailDto);
        
        return code;
    }
}
