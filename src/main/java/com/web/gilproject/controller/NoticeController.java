package com.web.gilproject.controller;

import com.web.gilproject.dto.NoticeDTO;
import com.web.gilproject.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/")
    public ResponseEntity<List<NoticeDTO>> notice() {
//        System.out.println("NoticeController");

        return ResponseEntity.ok(noticeService.getAllNotice());
    }
}
