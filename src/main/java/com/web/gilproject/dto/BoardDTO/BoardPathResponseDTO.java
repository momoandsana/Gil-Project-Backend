package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Path;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BoardPathResponseDTO(
        Long pathId,
        String title,
        String content,
        Integer time,
        double distance,
        double startLat,
        double startLong,
        LocalDateTime createdDate,
        String startAddr,

        List<Map<String, Double>> pathCoordinates,
        List<Map<String, Object>> pins
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
                path.getCreatedDate(),
                path.getStartAddr(),
                //동 추가
                Arrays.stream(path.getRoute().getCoordinates())
                        .map(coord -> Map.of("latitude", coord.y, "longitude", coord.x))
                        .toList(),
                path.getPins().stream()
                        .map(pin -> {
                            Map<String, Object> pinData = new HashMap<>();
                            pinData.put("id", pin.getId());
                            pinData.put("imageUrl", pin.getImageUrl());
                            pinData.put("content", pin.getContent());
                            pinData.put("latitude", pin.getLatitude());
                            pinData.put("longitude", pin.getLongitude());
                            return pinData;
                        })
                        .toList()

        );
    }
}
