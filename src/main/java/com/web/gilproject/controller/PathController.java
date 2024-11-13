package com.web.gilproject.controller;

import com.web.gilproject.domain.Path;
import com.web.gilproject.dto.CoordinateDto;
import com.web.gilproject.dto.PathDTO;
import com.web.gilproject.service.PathService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private LineString getLineString(List<CoordinateDto> coordinateDtos){
        Coordinate[] coordinates = new Coordinate[coordinateDtos.size()];
        for(int i = 0; i < coordinateDtos.size(); i++){
            coordinates[i] = new Coordinate(Double.parseDouble(coordinateDtos.get(i).getLatitude()), Double.parseDouble(coordinateDtos.get(i).getLongitude()));
        }
        LineString lineString = geometryFactory.createLineString(coordinates);
        return lineString;
    }

    // 예시: LineString의 시작점 구하기
    public Coordinate getStartPoint(LineString lineString) {
        // 시작점은 첫 번째 좌표이므로 index 0을 사용
        return lineString.getCoordinateN(0);
    }

    @PostMapping("/")
    public void insert(@RequestBody PathDTO paramPath){
        /*Point position = getPoint(Double.parseDouble(routeCoordinates.getX()),Double.parseDouble(routeCoordinates.getY()));*/

        LineString lineString = getLineString(paramPath.getRouteCoordinates());

        Coordinate startPoint = getStartPoint(lineString);

        Path path = Path.builder()
                .user(paramPath.getUser())
                .content(paramPath.getContent())
                .state(paramPath.getState())
                .title(paramPath.getTitle())
                .time(paramPath.getTime())
                .distance(paramPath.getDistance())
                .startLong(startPoint.x)
                .startLat(startPoint.y)
                .route(lineString).build();
        pathService.insert(path);
    }


    @GetMapping("/")
    //루트만 가져오는 방식.
    public ResponseEntity<?> findRouteByUserId() {
        List<String> wktStrings = pathService.getAllRoutesAsWKT();
        GeometryFactory geometryFactory = new GeometryFactory();

        // 위도와 경도만 담은 DTO 리스트로 변환하여 반환
        List<List<Map<String, Double>>> parsedCoordinates = new ArrayList<>();
        for (String wkt : wktStrings) {
            String coordinatesPart = wkt.replace("LINESTRING(", "").replace(")", "");

            List<Map<String, Double>> points = Arrays.stream(coordinatesPart.split(","))
                    .map(coord -> {
                        String[] latLon = coord.trim().split(" ");
                        Map<String, Double> pointMap = new HashMap<>();
                        try {
                            double latitude = Double.parseDouble(latLon[0]);
                            double longitude = Double.parseDouble(latLon[1]);
                            pointMap.put("latitude", latitude);
                            pointMap.put("longitude", longitude);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.err.println("Invalid coordinate format: " + coord);
                            // 오류가 발생한 경우 기본값 설정
                            pointMap.put("latitude", 0.0);
                            pointMap.put("longitude", 0.0);
                        }
                        return pointMap;
                    })
                    .collect(Collectors.toList());

            parsedCoordinates.add(points);
        }

        return ResponseEntity.status(HttpStatus.OK).body(parsedCoordinates);
    }
}
