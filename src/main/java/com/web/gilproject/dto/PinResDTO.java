package com.web.gilproject.dto;

import lombok.Data;

@Data
public class PinResDTO {
    private Long id;
    private String imageUrl;
    private String content;
    private double latitude;
    private double longitude;
}
