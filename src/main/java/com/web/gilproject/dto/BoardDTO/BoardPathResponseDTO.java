package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Path;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record BoardPathResponseDTO(
        Long routeId,
        String title,
        String description,
        Integer recordedTime,
        double distance,
        double startLat,
        double startLong,
        List<Map<String, Double>> routeCoordinates
) {
    public static BoardPathResponseDTO from(Path path) {
        return new BoardPathResponseDTO(
                path.getId(),
                path.getTitle(),
                path.getContent(),
                path.getTime(),
                path.getDistance(),
                path.getStartLat(),
                path.getStartLong(),
                Arrays.stream(path.getRoute().getCoordinates())
                        .map(coord -> Map.of("lat", coord.y, "lng", coord.x))
                        .toList()
        );
    }
}
