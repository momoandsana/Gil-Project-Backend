package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CoordinateResDTO;
import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.dto.PinResDTO;
import com.web.gilproject.dto.UserResDTO;
import com.web.gilproject.exception.PathErrorCode;
import com.web.gilproject.exception.PathPinException;
import com.web.gilproject.repository.PathRepository;
import com.web.gilproject.repository.UserRepository_jg;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PathService {

    private final PathRepository pathRepository;

    private final UserRepository_jg userRepository;


    //경로등록
    public Path insert(Path path) {
        Path savedPath= pathRepository.save(path);
        return savedPath;
    }

    //임시 유저찾기 - 형우꺼 완성되면 형우꺼 쓸 수도 있음
    public User findById(User user) {
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new PathPinException(PathErrorCode.NOTFOUND_USERID));
    }

    //유저 아이디로 유저찾기
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PathPinException(PathErrorCode.NOTFOUND_USERID));
    }

    //LineString변환없이 해당유저가 있는지 확인하려고 만든 메소드
    public List<Path> findPathByUserId(Long userId) {
        List<Path> pathList = pathRepository.findPathByUserId(userId);

        if (pathList == null || pathList.isEmpty()) {
            return Collections.emptyList();
        }

        return pathList;
    }

    //LineString 같은게 있는지 확인하려고 만든 메소드
    public List<Path> findPathByRoute(LineString route) {

        return pathRepository.findPathByRoute(route);
    }

    //LineString변환 후 Controller로 넘기는 메소드
    public List<PathResDTO> findPathByUserIdTransform(Long userId) {


        List<Path> list = pathRepository.findPathByUserId(userId);

        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }


            return list.stream()
                    .map(path -> {
                        PathResDTO pathDTO = new PathResDTO();

                        UserResDTO userDTO = new UserResDTO();
                        userDTO.setId(path.getUser().getId());
                        pathDTO.setUser(userDTO);

                        pathDTO.setId(path.getId());
                        pathDTO.setContent(path.getContent());
                        pathDTO.setState(path.getState());
                        pathDTO.setTitle(path.getTitle());
                        pathDTO.setTime(path.getTime());
                        pathDTO.setDistance(path.getDistance());
                        pathDTO.setCreateDate(path.getCreatedDate());
                        pathDTO.setStartLat(path.getStartLat());
                        pathDTO.setStartLong(path.getStartLong());
                        pathDTO.setStartAddr(path.getStartAddr());

                        pathDTO.setRouteCoordinates(
                                Arrays.stream(path.getRoute().getCoordinates())
                                        .map(coordinate -> {
                                            CoordinateResDTO coordDto = new CoordinateResDTO();
                                            coordDto.setLatitude(String.valueOf(coordinate.y)); // 현재 y를 latitude로 설정
                                            coordDto.setLongitude(String.valueOf(coordinate.x)); // 현재 x를 longitude로 설정
                                            return coordDto;
                                        }).collect(Collectors.toList())
                        );
                        //System.out.println("path.getPins() = " + path.getPins());
                        pathDTO.setPins(
                                path.getPins() != null ?
                                        path.getPins().stream()
                                                .map(pin -> {
                                                    PinResDTO pinDTO = new PinResDTO();
                                                    pinDTO.setId(pin.getId());
                                                    pinDTO.setImageUrl(pin.getImageUrl());
                                                    pinDTO.setContent(pin.getContent());
                                                    pinDTO.setLatitude(pin.getLatitude());
                                                    pinDTO.setLongitude(pin.getLongitude());
                                                    return pinDTO;
                                                }).collect(Collectors.toList())
                                        : Collections.emptyList()
                        );

                        return pathDTO;
                    })
                    .collect(Collectors.toList());

    }

    public PathResDTO getOnePath(Long pathId) {
        Path path = pathRepository.findById(pathId)
                .orElseThrow(() -> new PathPinException(PathErrorCode.NOTFOUND_PATH));

        // User 엔티티를 가져와서 DTO로 변환
        User user = userRepository.findByPathId(pathId);
        UserResDTO userDTO = new UserResDTO();
        userDTO.setId(user.getId());
        // 필요한 다른 user 필드들도 설정

        PathResDTO pathDTO = new PathResDTO();
        pathDTO.setUser(userDTO);

        pathDTO.setId(path.getId());
        pathDTO.setContent(path.getContent());
        pathDTO.setState(path.getState());
        pathDTO.setTitle(path.getTitle());
        pathDTO.setTime(path.getTime());
        pathDTO.setDistance(path.getDistance());
        pathDTO.setCreateDate(path.getCreatedDate());
        pathDTO.setStartLat(path.getStartLat());
        pathDTO.setStartLong(path.getStartLong());
        pathDTO.setStartAddr(path.getStartAddr());


        pathDTO.setRouteCoordinates(
                Arrays.stream(path.getRoute().getCoordinates())
                        .map(coordinate -> {
                            CoordinateResDTO coordDto = new CoordinateResDTO();
                            coordDto.setLatitude(String.valueOf(coordinate.y));
                            coordDto.setLongitude(String.valueOf(coordinate.x));
                            return coordDto;
                        }).collect(Collectors.toList())
        );

        pathDTO.setPins(
                path.getPins() != null ?
                        path.getPins().stream()
                                .map(pin -> {
                                    PinResDTO pinDTO = new PinResDTO();
                                    pinDTO.setId(pin.getId());
                                    pinDTO.setImageUrl(pin.getImageUrl());
                                    pinDTO.setContent(pin.getContent());
                                    pinDTO.setLatitude(pin.getLatitude());
                                    pinDTO.setLongitude(pin.getLongitude());
                                    return pinDTO;
                                }).collect(Collectors.toList())
                        : Collections.emptyList()
        );

        return pathDTO;
    }

    //전체경로 내뱉기
    public List<PathResDTO> findPathAllTransform() {

        List<Path> list = pathRepository.findAllState(); // JPQL에서 JOIN FETCH 사용
        //System.out.println(list);
        if(list != null && !list.isEmpty()) {
            return list.stream()
                    .map(path -> {
                        PathResDTO pathDTO = new PathResDTO();

                        UserResDTO userDTO = new UserResDTO();
                        userDTO.setId(path.getUser().getId());
                        pathDTO.setUser(userDTO);

                        pathDTO.setId(path.getId());
                        pathDTO.setContent(path.getContent());
                        pathDTO.setState(path.getState());
                        pathDTO.setTitle(path.getTitle());
                        pathDTO.setTime(path.getTime());
                        pathDTO.setDistance(path.getDistance());
                        pathDTO.setCreateDate(path.getCreatedDate());
                        pathDTO.setStartLat(path.getStartLat());
                        pathDTO.setStartLong(path.getStartLong());
                        pathDTO.setStartAddr(path.getStartAddr());

                        pathDTO.setRouteCoordinates(
                                Arrays.stream(path.getRoute().getCoordinates())
                                        .map(coordinate -> {
                                            CoordinateResDTO coordDto = new CoordinateResDTO();
                                            coordDto.setLatitude(String.valueOf(coordinate.y));
                                            coordDto.setLongitude(String.valueOf(coordinate.x));
                                            return coordDto;
                                        }).collect(Collectors.toList())
                        );
                        //System.out.println("path.getPins() = " + path.getPins());
                        pathDTO.setPins(
                                path.getPins() != null ?
                                        path.getPins().stream()
                                                .map(pin -> {
                                                    PinResDTO pinDTO = new PinResDTO();
                                                    pinDTO.setId(pin.getId());
                                                    pinDTO.setImageUrl(pin.getImageUrl());
                                                    pinDTO.setContent(pin.getContent());
                                                    pinDTO.setLatitude(pin.getLatitude());
                                                    pinDTO.setLongitude(pin.getLongitude());
                                                    return pinDTO;
                                                }).collect(Collectors.toList())
                                        : Collections.emptyList()
                        );

                        return pathDTO;
                    })
                    .collect(Collectors.toList());
        }
        else{
            return Collections.emptyList();
        }
    }

    //Path받아서 디코딩 후 PathResDTO로 리턴하는 메소드
    public PathResDTO decodingPath(Path path) {
        if (path == null) {
            throw new PathPinException(PathErrorCode.NOTFOUND_PATH);
        }

        PathResDTO pathDTO = new PathResDTO();

        UserResDTO userDTO = new UserResDTO();
        userDTO.setId(path.getUser().getId());
        pathDTO.setUser(userDTO);

        pathDTO.setId(path.getId());
        pathDTO.setContent(path.getContent());
        pathDTO.setState(path.getState());
        pathDTO.setTitle(path.getTitle());
        pathDTO.setTime(path.getTime());
        pathDTO.setDistance(path.getDistance());
        pathDTO.setCreateDate(path.getCreatedDate());
        pathDTO.setStartLat(path.getStartLat());
        pathDTO.setStartLong(path.getStartLong());
        pathDTO.setStartAddr(path.getStartAddr());

        pathDTO.setRouteCoordinates(
                Arrays.stream(path.getRoute().getCoordinates())
                        .map(coordinate -> {
                            CoordinateResDTO coordDto = new CoordinateResDTO();
                            coordDto.setLatitude(String.valueOf(coordinate.y)); // 위도
                            coordDto.setLongitude(String.valueOf(coordinate.x)); // 경도
                            return coordDto;
                        }).collect(Collectors.toList())
        );

        pathDTO.setPins(
                path.getPins() != null ?
                        path.getPins().stream()
                                .map(pin -> {
                                    PinResDTO pinDTO = new PinResDTO();
                                    pinDTO.setId(pin.getId());
                                    pinDTO.setImageUrl(pin.getImageUrl());
                                    pinDTO.setContent(pin.getContent());
                                    pinDTO.setLatitude(pin.getLatitude());
                                    pinDTO.setLongitude(pin.getLongitude());
                                    return pinDTO;
                                }).collect(Collectors.toList())
                        : Collections.emptyList()
        );

        return pathDTO;
    }


    //Path State값 변경하기.
    public void updateState(Long pathId, int state) {

        Path path = pathRepository.findById(pathId)
                .orElseThrow(() -> new PathPinException(PathErrorCode.PATH_UPDATE_FAILED));

        path.setState(state);

    }
}