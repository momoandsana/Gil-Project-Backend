package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.repository.GilListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GilListServiceImpl implements GilListService {

    private final GilListRepository gilListRepository;

    @Override
    public List<Post> findByMyPosition(double nowY, double nowX) {
        List<Post> allListPost = this.findAll(); //전체 게시글 조회
        List<Post> nearMe = new ArrayList<>(); //결과 게시글 리스트

        for(Post post : allListPost){
            double startLat = post.getPath().getStartLat(); //시작위도
            double startLong = post.getPath().getStartLong(); //시작경도
            this.distance(nowY, nowX, startLat, startLong);

        }
        return nearMe;
    }

    /**
     * 전체 게시글 조회
     * */
    public List<Post> findAll(){
        return gilListRepository.findAll();
    }

    /**
     * 두 좌표 사이의 거리를 구하는 공식
     * lat1: 지점 1 위도
     * lat2: 지점 2 위도
     * lon1: 지점 1 경도
     * lon2: 지점 2 경도
     * */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // 위도와 경도를 사용하여 두 지점 사이의 경도 차이를 구함
        double theta = lon1 - lon2;

        // 위도와 경도를 라디안으로 변환하여 두 지점 간의 거리를 구하는 삼각함수 계산
        double dist = Math.sin(deg2rad(lat1))*Math.sin(deg2rad(lat2))+Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));

        dist = Math.acos(dist); // acos 함수를 사용하여 아크코사인 값(호의 각도)을 구함
        dist = rad2deg(dist); // radian 값을 각도로 변환
        dist = dist * 60 * 1.1515; // 거리 계산: 위도 1도가 약 60해리(1해리 = 1.1515마일)임을 이용하여 마일 단위로 변환

        dist = dist * 1.609344; // 마일을 킬로미터로 변환
        return dist; // 최종 거리(킬로미터)를 반환
    }

    /**
     * decimal 각도를 라디언 값으로 변환
     * */
    private static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    /**
     * 라디언 값을 decimal 각도로 변환
     * */
    private static double rad2deg(double rad) {
        return rad * 180 / Math.PI;
    }
}
