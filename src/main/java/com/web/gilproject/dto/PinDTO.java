package com.web.gilproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PinDTO {
    private Long id;
    private Long pathId;
    private String imageUrl;
    private String content;
    private String latitude;
    private String longitude;
    private int state;
}
