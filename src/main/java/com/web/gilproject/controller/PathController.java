package com.web.gilproject.controller;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Pin;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.*;
import com.web.gilproject.exception.ErrorCode;
import com.web.gilproject.exception.MemberAuthenticationException;
import com.web.gilproject.exception.PathErrorCode;
import com.web.gilproject.exception.PathPinException;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.PathService;
import com.web.gilproject.service.PinService;
import com.web.gilproject.service.PostWishlistServiceImpl;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/path")
public class PathController {

    private final PathService pathService;

    private final PinService pinService;

    private final AmazonService s3Service;

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    //넘어오는 위도경도 list를 LineString으로 묶기.
    private LineString getLineString(List<CoordinateDto> coordinateDtos) {
        Coordinate[] coordinates = new Coordinate[coordinateDtos.size()];
        for (int i = 0; i < coordinateDtos.size(); i++) {
            coordinates[i] = new Coordinate(
                    Double.parseDouble(coordinateDtos.get(i).getLongitude()), // x = 위도 Latitude()
                    Double.parseDouble(coordinateDtos.get(i).getLatitude())   // y = 경도 Longitude()
            );
        }
        return geometryFactory.createLineString(coordinates);
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
            throw new PathPinException(PathErrorCode.IMAGE_TRANS_FAILED);
        }
    }

    //경로(Path)등록
    @PostMapping("/")
    public void insert(@RequestBody PathDTO paramPath, Authentication authentication) {
        System.out.println(paramPath);

        // LineString 생성
        LineString lineString = getLineString(paramPath.getRouteCoordinates());

        // route로 경로 조회
        List<Path> pathList = pathService.findPathByRoute(lineString);

        Path path;
        if (pathList == null || pathList.isEmpty()) {

            CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = customMemberDetails.getId();
            if(userId == null)
            {
                throw new MemberAuthenticationException(ErrorCode.NOTFOUND_USER);
            }
            // 기존 경로가 없으면 새로 생성
            path = Path.builder()
                    .user(pathService.findUserById(userId))
                    .content(paramPath.getContent())
                    .state(paramPath.getState() != null ? paramPath.getState() : 0)
                    .title(paramPath.getTitle())
                    .time(paramPath.getTime())
                    .distance(paramPath.getDistance())
                    .startLong(paramPath.getStartLong())
                    .startLat(paramPath.getStartLat())
                    .startAddr(paramPath.getStartAddr())
                    .route(lineString)
                    .build();

            System.out.println(path.getUser());
            System.out.println(path.getContent());
            System.out.println(path.getState());
            System.out.println(path.getTitle());
            System.out.println(path.getTime());
            System.out.println(path.getDistance());
            System.out.println(path.getStartLong());
            System.out.println(path.getStartLat());
            System.out.println(path.getStartAddr());
            System.out.println(path.getRoute());
            System.out.println(path.getCreatedDate());

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
                    // Base64 이미지를 MultipartFile로 변환
                    MultipartFile imageFile = convertBase64ToMultipartFile(pinDTO.getImageUrl());

                    // 고유 파일 이름 생성 (UUID 사용)
                    String uniqueFileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();

                    // 폴더 이름 결합
                    String folderName = "pin_images";
                    String filePath = folderName + "/" + uniqueFileName;

                    // S3에 업로드
                    imageUrl = s3Service.uploadFileToFolder(imageFile, filePath);
                }

                // Pin 엔티티 생성 및 저장
                Pin pin = Pin.builder()
                        .path(finalPath)
                        .content(pinDTO.getContent())
                        .latitude(pinDTO.getLatitude())
                        .longitude(pinDTO.getLongitude())
                        .imageUrl(imageUrl)
                        .build();

                pinService.insert(pin); // Pin을 DB에 저장
            } catch (Exception e) {
                throw new PathPinException(PathErrorCode.IMAGE_TRANS_FAILED);
            }
        });
    }

    //프론트에서 넘겨받은 userId로 경로+핀 뿌리기
    @GetMapping("/{userId}")
    public ResponseEntity<?> getPath(@PathVariable Long userId) {
        List<PathResDTO> pathResDTOList = pathService.findPathByUserIdTransform(userId);

        return ResponseEntity.ok(pathResDTOList);
    }

    //프론트에서 넘겨받은 pathId로 하나의 경로+핀 뿌리기
    @GetMapping("/follow/{pathId}")
    public ResponseEntity<?> getOnePath(@PathVariable Long pathId) {
        PathResDTO pathResDTO = pathService.getOnePath(pathId);

        return ResponseEntity.ok(pathResDTO);
    }

    //전체 경로+핀 뿌리기
    @GetMapping("/")
    public ResponseEntity<?> getPathAll() {
        List<PathResDTO> pathResDTOList = pathService.findPathAllTransform();

        return ResponseEntity.ok(pathResDTOList);
    }

    //핀 업데이트
    /*@PutMapping("/pin/{pinId}")
    public void pinUpdate(@PathVariable Long pinId, @RequestBody PinResDTO pinResDTO) {
        try {
            // Base64로 전달된 이미지 처리
            String imageUrl = null;
            if (pinResDTO.getImageUrl() != null && !pinResDTO.getImageUrl().isEmpty()) {
                MultipartFile imageFile = convertBase64ToMultipartFile(pinResDTO.getImageUrl());
                imageUrl = s3Service.uploadFile(imageFile);
            }

            // PinResDTO에 이미지 URL 설정
            if (imageUrl != null) {
                pinResDTO.setImageUrl(imageUrl);
            }

            // Pin 업데이트
            pinService.update(pinId, pinResDTO);
        } catch (Exception e) {
            throw new PathPinException(PathErrorCode.PIN_UPDATE_FAILED);
        }
    }*/

    //핀 삭제
    @DeleteMapping("/pin/{pinId}")
    public void pinDelete(@PathVariable Long pinId) {
        pinService.delete(pinId);
    }


    //path삭제하기.
    @DeleteMapping("/{pathId}")
    public void deletePath(@PathVariable Long pathId) {

        pathService.deletePath(pathId);
    }



}


