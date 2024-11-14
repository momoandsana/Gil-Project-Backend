package com.web.gilproject.dto;

import com.web.gilproject.domain.Pin;
import com.web.gilproject.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathDTO {
    private Long id;
    private User user;
    private Long userId;
    private Integer state;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private Integer time;
    private Double distance;
    private Double startLat;
    private Double startLong;
    private List<CoordinateDto> routeCoordinates;
    private Set<Pin> pins;

}
