package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Path;

import java.util.Arrays;
import java.util.List;

public record BoardPathDTO(
        Long id,
        String title,
        Integer time,
        double distance,
        double startLat,
        double startLong,
        List<double[]> route
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
                Arrays.stream(path.getRoute().getCoordinates())
                        .map(coord->new double[]{coord.x,coord.y})
                        .toList()
        );
    }
}
