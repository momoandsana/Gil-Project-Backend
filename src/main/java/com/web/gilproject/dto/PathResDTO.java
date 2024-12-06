package com.web.gilproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathResDTO {
    private Long id;
    private UserResDTO user;
    private String content;
    private Integer state;
    private String title;
    private int time;
    private LocalDateTime createDate;
    private double distance;
    private double startLat;
    private double startLong;
    private String startAddr;
    private List<CoordinateResDTO> routeCoordinates;
    private List<PinResDTO> pins;


}

