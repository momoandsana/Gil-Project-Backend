package com.web.gilproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class PathResDTO {
    private Long id;
    private UserResDTO user;
    private String content;
    private Integer state;
    private String title;
    private int time;
    private double distance;
    private double startLat;
    private double startLong;
    private String startAddr;
    private List<CoordinateResDTO> routeCoordinates;
    private List<PinResDTO> pins;
}

