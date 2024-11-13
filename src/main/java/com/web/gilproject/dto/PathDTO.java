package com.web.gilproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathDTO {
    private double id;
    private double userId;
    private int state;
    private String title;
    private String content;
    private String createDate;
    private int time;
    private double distance;
    private double startLat;
    private double startLong;
    private List<CoordinateDto> routeCoordinates;

}
