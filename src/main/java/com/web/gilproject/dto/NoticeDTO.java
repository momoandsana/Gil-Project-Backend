package com.web.gilproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeDTO {
    private String title;
    private String content;
    private LocalDateTime writeDate;
}
