package com.web.gilproject.controller;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Pin;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CoordinateDto;
import com.web.gilproject.dto.PathDTO;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.PathService;
import com.web.gilproject.service.PinService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    private final PinService pinService;

    private final AmazonService s3Service;

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    //넘어오는 위도경도 list를 LineString으로 묶기.
    private LineString getLineString(List<CoordinateDto> coordinateDtos){
        Coordinate[] coordinates = new Coordinate[coordinateDtos.size()];
        for(int i = 0; i < coordinateDtos.size(); i++){
            coordinates[i] = new Coordinate(Double.parseDouble(coordinateDtos.get(i).getLatitude()), Double.parseDouble(coordinateDtos.get(i).getLongitude()));
        }
        LineString lineString = geometryFactory.createLineString(coordinates);
        return lineString;
    }

    //LineString의 시작점 구하기
    public Coordinate getStartPoint(LineString lineString) {
        // 시작점은 첫 번째 좌표이므로 index 0을 사용
        return lineString.getCoordinateN(0);
    }

    //base64로 들어온 이미지 다시 변환하여 s3에 넣기.
    public static MultipartFile convertBase64ToMultipartFile(String base64Image) {
        try {
            // Base64 문자열에서 "data:image/jpeg;base64," 같은 접두사 제거
            String[] parts = base64Image.split(",");
            String imageString = parts.length > 1 ? parts[1] : parts[0];
            byte[] imageBytes = Base64.getDecoder().decode(imageString);

            // MockMultipartFile을 사용하여 MultipartFile 생성
            return new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert Base64 to MultipartFile", e);
        }
    }

    //경로와 핀 등록.
    @PostMapping("/")
    public void insert(@RequestBody PathDTO paramPath) {
        System.out.println(paramPath);

        // 1. userId로 사용자 조회
        User user = pathService.findById(paramPath.getUser());

        // 2. 경로가 이미 존재하는지 확인
        List<Path> pathList = pathService.findPathByUserId(user.getId());

        // 3. path 초기화
        Path path;
        if (pathList == null || pathList.isEmpty()) {
            // 기존 경로가 없으면 새로 생성
            LineString lineString = getLineString(paramPath.getRouteCoordinates());
            Coordinate startPoint = getStartPoint(lineString);

            path = Path.builder()
                    .user(user)
                    .content(paramPath.getContent())
                    .state(paramPath.getState())
                    .title(paramPath.getTitle())
                    .time(paramPath.getTime())
                    .distance(paramPath.getDistance())
                    .startLong(startPoint.x)
                    .startLat(startPoint.y)
                    .route(lineString)
                    .build();

            // 새 Path 저장
            pathService.insert(path);
        } else {
            // 기존 경로가 존재하면 첫 번째 항목을 사용
            path = pathList.get(0);
        }

        // 4. 새로운 핀 데이터 저장
        final Path finalPath = path;
        paramPath.getPins().forEach(pinDTO -> {
            try {
                System.out.println("imageFile 1번 = " + pinDTO.getImageUrl());
                String imageUrl = null;

                // Base64 이미지를 MultipartFile로 변환하고 S3에 업로드
                if (pinDTO.getImageUrl() != null && !pinDTO.getImageUrl().isEmpty()) {
                    MultipartFile imageFile = convertBase64ToMultipartFile(pinDTO.getImageUrl());
                    System.out.println("imageFile 2번 = " + imageFile);
                    imageUrl = s3Service.uploadFile(imageFile);
                }

                // 빌더를 사용하여 Pin 객체 생성
                Pin pin = Pin.builder()
                        .path(finalPath)
                        .content(pinDTO.getContent())
                        .latitude(pinDTO.getLatitude())
                        .longitude(pinDTO.getLongitude())
                        .imageUrl(imageUrl)
                        .build();

                pinService.insert(pin); // Pin을 DB에 저장
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to decode Base64 image and upload");
            }
        });
    }


    /*@GetMapping("/")
    //해당 유저의 루트만 가져오는 방식.
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
    }*/
}
