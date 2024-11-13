package com.web.gilproject.dto;

import com.web.gilproject.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathDTO {
    private double id;
    private User user;
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
