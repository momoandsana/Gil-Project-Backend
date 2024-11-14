package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Path;
import org.locationtech.jts.geom.LineString;

public record BoardPathDTO(
        Long id,
        String title,
        Integer time,
        double distance,
        double startLat,
        double startLong,
        LineString route
)
{
    public static BoardPathDTO from(Path path)
    {
        return new BoardPathDTO(
                path.getId(),
                path.getTitle(),
                path.getTime(),
                path.getDistance(),
                path.getStartLat(),
                path.getStartLong(),
                path.getRoute()
        );
    }
}
