package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.*;
import com.web.gilproject.repository.PathRepository;
import com.web.gilproject.repository.UserRepository_jg;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + user.getId()));
    }

    //LineString변환없이 해당유저가 있는지 확인하려고 만든 메소드
    public List<Path> findPathByUserId(Long userId) {
        return pathRepository.findPathByUserId(userId);
    }

    //LineString 같은게 있는지 확인하려고 만든 메소드
    public List<Path> findPathByRoute(LineString route) {
        return pathRepository.findPathByRoute(route);
    }

    //LineString변환 후 Controller로 넘기는 메소드
    public List<PathResDTO> findPathByUserIdTransform(Long userId) {

        List<Path> list = pathRepository.findPathByUserId(userId); // JPQL에서 JOIN FETCH 사용
        //System.out.println(list);

        return list.stream()
                .map(path -> {
                    PathResDTO pathDTO = new PathResDTO();

                    UserResDTO userDTO = new UserResDTO();
                    userDTO.setId(path.getUser().getId());
                    pathDTO.setUser(userDTO);

                    pathDTO.setContent(path.getContent());
                    pathDTO.setState(path.getState());
                    pathDTO.setTitle(path.getTitle());
                    pathDTO.setTime(path.getTime());
                    pathDTO.setDistance(path.getDistance());
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

}