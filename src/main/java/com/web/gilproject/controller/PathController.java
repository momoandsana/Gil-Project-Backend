package com.web.gilproject.controller;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Pin;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CoordinateDto;
import com.web.gilproject.dto.PathDTO;
import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.PathService;
import com.web.gilproject.service.PinService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
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

    /*//LineString의 시작점 구하기
    public Coordinate getStartPoint(LineString lineString) {
        // 시작점은 첫 번째 좌표이므로 index 0을 사용
        return lineString.getCoordinateN(0);
    }*/

    //base64로 들어온 이미지 다시 변환하여 s3에 넣기.
    public static MultipartFile convertBase64ToMultipartFile(String base64Image) {
        try {
            String[] parts = base64Image.split(",");
            String imageString = parts.length > 1 ? parts[1] : parts[0];
            byte[] imageBytes = Base64.getDecoder().decode(imageString);

            return new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert Base64 to MultipartFile", e);
        }
    }

    @PostMapping("/")
    public void insert(@RequestBody PathDTO paramPath) {
        System.out.println(paramPath);

        // LineString 생성
        LineString lineString = getLineString(paramPath.getRouteCoordinates());

        // route로 경로 조회
        List<Path> pathList = pathService.findPathByRoute(lineString);

        Path path;
        if (pathList == null || pathList.isEmpty()) {
            // 기존 경로가 없으면 새로 생성
            path = Path.builder()
                    .user(pathService.findById(paramPath.getUser()))
                    .content(paramPath.getContent())
                    .state(paramPath.getState())
                    .title(paramPath.getTitle())
                    .time(paramPath.getTime())
                    .distance(paramPath.getDistance())
                    .startLong(paramPath.getStartLong())
                    .startLat(paramPath.getStartLat())
                    .startAddr(paramPath.getStartAddr())
                    .route(lineString)
                    .build();

            pathService.insert(path);
        } else {
            path = pathList.get(0); // 기존 경로 사용
        }

        final Path finalPath = path;

        // Pin 데이터 처리
        paramPath.getPins().forEach(pinDTO -> {
            try {
                String imageUrl = null;

                if (pinDTO.getImageUrl() != null && !pinDTO.getImageUrl().isEmpty()) {
                    MultipartFile imageFile = convertBase64ToMultipartFile(pinDTO.getImageUrl());
                    imageUrl = s3Service.uploadFile(imageFile);
                }

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

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPath(@PathVariable Long userId) {
        List<PathResDTO> pathResDTOList = pathService.findPathByUserIdTransform(userId);

        return ResponseEntity.ok(pathResDTOList);
    }
}
