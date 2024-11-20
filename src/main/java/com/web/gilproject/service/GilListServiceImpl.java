package com.web.gilproject.service;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.repository.GilListRepository;
import com.web.gilproject.repository.UserRepository_YJ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private final PathService pathService;

    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByMyPosition(Double nowY, Double nowX, Pageable pageable) {
        //전체 조회
        Page<PostDTO> pagePostDTO = gilListRepository.findAllPostDTO(pageable);

        //내 위치로부터 5km이내의 산책길 글목록만 남기기
        List<PostDTO> filteredPosts = pagePostDTO.getContent().stream()
                .filter(post -> {
                    Double startLat = post.getStartLat();
                    Double startLong = post.getStartLong();
                    Double difference = this.distance(nowY, nowX, startLat, startLong);
                    return difference < 5.0;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(this.changeList(filteredPosts), pageable, filteredPosts.size());
    }

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByNearAddr(Authentication authentication, Pageable pageable) {

        //현재 로그인 중인 유저의 id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        //해당 id인 유저의 집주소 위도와 경도 알아내기
        Double homeLat = userRepository.findByUserId(userId).getLatitude(); //집주소 위도
        Double homeLong = userRepository.findByUserId(userId).getLongitude(); //집주소 경도

        //전체조회
        Page<PostDTO> pagePostDTO = gilListRepository.findAllPostDTO(pageable);

        //내 집주소로부터 반경 5km이내의 산책길 글목록만 남기기
        List<PostDTO> filteredPosts = pagePostDTO.getContent().stream()
                .filter(post -> {
                    Double startLat = post.getStartLat();
                    Double startLong = post.getStartLong();
                    Double difference = this.distance(homeLat, homeLong, startLat, startLong);
                    return difference < 5.0;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(
                this.changeList(filteredPosts),
                pageable,
                filteredPosts.size()
        );
    }

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByNickName(String nickName, Pageable pageable) {

        //작성자 닉네임에 의한 산책길 글목록 조회
        Page<PostDTO> pagePostDTO = gilListRepository.findByNickName(nickName, pageable);
        List<PostDTO> listPostDTO = pagePostDTO.getContent();

        return new PageImpl<>(
                this.changeList(listPostDTO),
                pageable,
                listPostDTO.size()
        );
    }

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findMyGilList(Authentication authentication, Pageable pageable) {

        //현재 로그인 중인 유저의 id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        //글쓴 유저 id에 의한 산책길 글목록 찾기
        Page<PostDTO> pagePostDTO = gilListRepository.findByUserId(userId, pageable);

        List<PostDTO> listPostDTO = pagePostDTO.getContent();

        return new PageImpl<>(
                this.changeList(listPostDTO),
                pageable,
                listPostDTO.size()
        );
    }

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findMyFav(Authentication authentication, Pageable pageable) {

        //현재 로그인 중인 유저의 Id를 찾아오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        //전체 산책길 글목록 조회
        Page<PostDTO> pagePostDTO = gilListRepository.findAllPostDTO(pageable);

        //해당 유저id가 찜한 목록만 조회
        List<PostDTO> listPostDTO = pagePostDTO.getContent().stream()
                .filter(post -> {
                    return post.getPostWishListsUsers().contains(userId);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(
                this.changeList(listPostDTO),
                pageable,
                listPostDTO.size()
        );

    }

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByKeyword(String keyword, Pageable pageable) {

        //키워드 검색(제목, 내용, 닉네임, 시작주소)
        Page<PostDTO> titleKeyword = gilListRepository.findByTitleContaining(keyword, pageable);
        Page<PostDTO> contentKeyword = gilListRepository.findByContentContaining(keyword, pageable);
        Page<PostDTO> nickNameKeyword = gilListRepository.findByNickNameContaining(keyword, pageable);
        Page<PostDTO> startAddrKeyword = gilListRepository.findByStartAddrContaining(keyword, pageable);

        //중복제거
        Set<PostDTO> combinedSet = new HashSet<>();
        combinedSet.addAll(titleKeyword.getContent());
        combinedSet.addAll(contentKeyword.getContent());
        combinedSet.addAll(nickNameKeyword.getContent());
        combinedSet.addAll(startAddrKeyword.getContent());

        //좋아요 많은 순으로 정렬
        List<PostDTO> combinedList = new ArrayList<>(combinedSet);
        combinedList.sort((p1, p2) -> p2.getPostLikesNum().compareTo(p1.getPostLikesNum()));

        // 전체 데이터 수 계산
        long total = combinedSet.size();

        // 페이징 처리: 현재 페이지 범위에 해당하는 데이터 슬라이싱
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedList.size());
        List<PostDTO> pagedResult = combinedList.subList(start, end);

        return new PageImpl<>(this.changeList(pagedResult), pageable, total);
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

    /**
     * List<PostDTO>를 List<PostResDTO>로 바꾸는 함수
     * */
    private List<PostResDTO> changeList(List<PostDTO> listPostDTO){
        List<PostResDTO> result = new ArrayList<>();
        for(PostDTO post : listPostDTO){
            Long id = post.getId();
            String userNickName = post.getUserNickName();
            Long pathId = post.getPathId();
            Double startLat = post.getStartLat();
            Double startLong = post.getStartLong();
            Integer state = post.getState();
            String title = post.getTitle();
            String content = post.getContent();
            String tag = post.getTag();
            LocalDateTime writeDate = post.getWriteDate();
            LocalDateTime updateDate = post.getUpdateDate();
            Integer readNum = post.getReadNum();
            List<Long> postLikesUsers = post.getPostLikesUsers();
            Integer postLikesNum = post.getPostLikesNum();
            List<Long> repliesUsers = post.getRepliesUsers();
            Integer repliesNum = post.getRepliesNum();
            List<Long> postWishListsUsers = post.getPostWishListsUsers();
            Integer postWishListsNum = post.getPostWishListsNum();
            String userImgURL = post.getUserImgURL();

            PathResDTO pathResDTO = pathService.decodingPath(post.getPath());

            result.add(new PostResDTO(id, userNickName, pathId, startLat, startLong, state, title, content, tag,
                    writeDate,updateDate,readNum,postLikesUsers,postLikesNum,repliesUsers,repliesNum,
                    postWishListsUsers, postWishListsNum, userImgURL, pathResDTO));
        }
        return result;
    }
}
