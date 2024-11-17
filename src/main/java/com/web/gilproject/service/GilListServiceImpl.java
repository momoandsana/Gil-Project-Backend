package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.PostDTO_YJ.PostDTO;
import com.web.gilproject.repository.GilListRepository;
import com.web.gilproject.repository.UserRepository_YJ;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GilListServiceImpl implements GilListService {

    private final GilListRepository gilListRepository;
    private final UserRepository_YJ userRepository;

    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> findByMyPosition(Double nowY, Double nowX) {
        //최종 결과 게시글 리스트
        List<PostDTO> nearMe = new ArrayList<>();

        //전체 게시글 리스트
        List<PostDTO> allListPostDTO = this.findAll().stream().map(PostDTO::new).collect(Collectors.toList());

        for(PostDTO post : allListPostDTO){
            Double startLat = post.getStartLat(); //시작위도
            Double startLong = post.getStartLong(); //시작경도

            //현재위치에서 각 게시글의 시작점까지의 거리
            Double difference = this.distance(nowY, nowX, startLat, startLong);
            if (difference<5.0){ //반경 5km이내에 시작점이 있을 경우에만 리턴한다.
                nearMe.add(post);
            }
        }
        return nearMe;
    }

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> findByNearAddr(Authentication authentication) {
        //최종 결과값을 담을 List
        List<PostDTO> nearHome = new ArrayList<>();

        //현재 로그인 중인 유저의 name을 찾아오기
        String name = authentication.getName();

        //해당 name인 유저의 집주소 위도와 경도 알아내기
        Double homeLat = userRepository.findByName(name).getLatitude(); //집주소 위도
        Double homeLong = userRepository.findByName(name).getLongitude(); //집주소 경도

        //전체 게시글 리스트 조회
        List<PostDTO> allListPostDTO = this.findAll().stream().map(PostDTO::new).collect(Collectors.toList());

        for(PostDTO post : allListPostDTO){
            Double startLat = post.getStartLat(); //시작위도
            Double startLong = post.getStartLong(); //시작경도

            //집주소에서 각 게시글의 시작점까지의 거리
            Double difference = this.distance(homeLat, homeLong, startLat, startLong);
            if (difference<5.0){ //반경 5km이내에 시작점이 있을 경우에만 리턴한다.
                nearHome.add(post);
            }
        }
        return nearHome;
    }

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> findByNickName(String nickName) {
        //최종결과값을 담을 List
        List<PostDTO> postByNickName = new ArrayList<>();

        //nickName이 완전히 일치하는 유저가 작성한 산책길 글 목록 불러오기
        postByNickName = gilListRepository.findByNickName(nickName);

        return postByNickName;
    }

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> findMyGilList(Authentication authentication) {
        //최종 결과값을 담을 List
        List<PostDTO> myGilList = new ArrayList<>();

        //현재 로그인 중인 유저의 이름
        String userName = authentication.getName();

        //유저 이름이 완전히 일치하는 유저가 작성한 산책길 글 목록 불러오기
        myGilList = gilListRepository.findByName(userName);

        return myGilList;
    }

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> findMyFav(Authentication authentication) {
        //최종 결과물 담을 List
        List<PostDTO> myFavList = new ArrayList<>();

        //현재 로그인 중인 유저 이름 가져오기
        String userName = authentication.getName();

        //유저 이름을 바탕으로 유저 id 가져오기
        Long userId = userRepository.findByName(userName).getId();

        //전체 게시글 리스트 조회
        List<PostDTO> allListPostDTO = this.findAll().stream().map(PostDTO::new).collect(Collectors.toList());

        //게시글을 찜한 유저의 목록을 조회
        for(PostDTO post : allListPostDTO){
            if(post.getPostWishListsUsers().contains(userId)){
                myFavList.add(post);
            }
        }

        return myFavList;
    }

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> findByKeyword(String keyword) {
        //최종 결과물을 담을 Set - 중복을 피하기 위해 Set 사용
        Set<PostDTO> postByKeyword = new HashSet<>();

        //제목에 따른 검색결과
        Set<PostDTO> titleKeyword = gilListRepository.findByTitleContaining(keyword);
        postByKeyword.addAll(titleKeyword);

        //글 내용에 따른 검색결과
        Set<PostDTO> contentKeyword = gilListRepository.findByContentContaining(keyword);
        postByKeyword.addAll(contentKeyword);

        //글 작성자 닉네임에 따른 검색결과
        Set<PostDTO> nickNameKeyword = gilListRepository.findByNickNameContaining(keyword);
        postByKeyword.addAll(nickNameKeyword);

        //산책길 시작점에 따른 검색결과
        Set<PostDTO> startAddrKeyword = gilListRepository.findByStartAddrContaining(keyword);
        postByKeyword.addAll(startAddrKeyword);

        List<PostDTO> result = new ArrayList<>();
        result.addAll(postByKeyword);

        //결과 List를 좋아요 내림차순 순서로 정렬
        result.sort((p1, p2)->p2.getPostLikesNum().compareTo(p1.getPostLikesNum()));

        return result;
    }

    /**
     * 전체 게시글 조회
     * */
    public List<Post> findAll(){
        System.out.println("전체게시글 조회하기"+gilListRepository.findAll());
        return gilListRepository.findAll();
    }

    /**
     * 두 좌표 사이의 거리를 구하는 공식
     * lat1: 지점 1 위도
     * lat2: 지점 2 위도
     * lon1: 지점 1 경도
     * lon2: 지점 2 경도
     * */
    private Double distance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // 위도와 경도를 사용하여 두 지점 사이의 경도 차이를 구함
        Double theta = lon1 - lon2;

        // 위도와 경도를 라디안으로 변환하여 두 지점 간의 거리를 구하는 삼각함수 계산
        Double dist = Math.sin(deg2rad(lat1))*Math.sin(deg2rad(lat2))+Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));

        dist = Math.acos(dist); // acos 함수를 사용하여 아크코사인 값(호의 각도)을 구함
        dist = rad2deg(dist); // radian 값을 각도로 변환
        dist = dist * 60 * 1.1515; // 거리 계산: 위도 1도가 약 60해리(1해리 = 1.1515마일)임을 이용하여 마일 단위로 변환

        dist = dist * 1.609344; // 마일을 킬로미터로 변환
        return dist; // 최종 거리(킬로미터)를 반환
    }

    /**
     * decimal 각도를 라디언 값으로 변환
     * */
    private static Double deg2rad(Double deg) {
        return deg * Math.PI / 180.0;
    }

    /**
     * 라디언 값을 decimal 각도로 변환
     * */
    private static Double rad2deg(Double rad) {
        return rad * 180 / Math.PI;
    }
}
