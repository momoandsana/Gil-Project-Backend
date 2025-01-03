package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.PostImage;
import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.repository.GilListRepository;
import com.web.gilproject.repository.UserRepository_YJ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GilListServiceImpl implements GilListService {

    private final GilListRepository gilListRepository;
    private final UserRepository_YJ userRepository;

    private final PathService pathService;
    private final ElasticsearchService elasticsearchService;

    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByMyPosition(Double nowY, Double nowX, Pageable pageable, Long userId) {
        // 전체 데이터 조회 후 List에 저장
        List<Post> allPostsList = new ArrayList<>(gilListRepository.findAllPostDTO(Pageable.unpaged()).getContent());

        // 내 위치로부터 1km 이내의 산책길 글목록만 필터링
        List<Post> filteredPostsList = allPostsList.stream()
                .filter(post -> {
                    Double startLat = post.getPath().getStartLat();
                    Double startLong = post.getPath().getStartLong();
                    Double difference = this.distance(nowY, nowX, startLat, startLong);
                    return difference < 1.0;
                })
                .collect(Collectors.toList());

        //좋아요 많은 순으로 정렬
        filteredPostsList.sort((p1, p2) -> Integer.compare(p2.getPostLikes().size(), p1.getPostLikes().size()));

        // 전체 데이터 수 계산
        long total = filteredPostsList.size();

        // 페이징 처리: 현재 페이지 범위에 해당하는 데이터 슬라이싱
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredPostsList.size());
        List<Post> pagedResult = filteredPostsList.subList(start, end);

        // 반환
        return new PageImpl<>(this.changeList(pagedResult, userId), pageable, total);
    }

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByNearAddr(Pageable pageable, Long userId) {

        //해당 id인 유저의 집주소 위도와 경도 알아내기
        Double homeLat = userRepository.findByUserId(userId).getLatitude(); //집주소 위도
        Double homeLong = userRepository.findByUserId(userId).getLongitude(); //집주소 경도

        // 전체 데이터 조회 후 List에 저장
        List<Post> allPostsList = new ArrayList<>(gilListRepository.findAllPostDTO(Pageable.unpaged()).getContent());

        //내 집주소로부터 반경 1km이내의 산책길 글목록만 남기기
        List<Post> filteredPostsList = allPostsList.stream()
                .filter(post -> {
                    Double startLat = post.getPath().getStartLat();
                    Double startLong = post.getPath().getStartLong();
                    Double difference = this.distance(homeLat, homeLong, startLat, startLong);
                    return difference < 1.0;
                })
                .collect(Collectors.toList());

        //좋아요 많은 순으로 정렬
        filteredPostsList.sort((p1, p2) -> Integer.compare(p2.getPostLikes().size(), p1.getPostLikes().size()));

        // 전체 데이터 수 계산
        long total = filteredPostsList.size();

        // 페이징 처리: 현재 페이지 범위에 해당하는 데이터 슬라이싱
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredPostsList.size());
        List<Post> pagedResult = filteredPostsList.subList(start, end);

        // 반환
        return new PageImpl<>(this.changeList(pagedResult, userId), pageable, total);
    }

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByNickName(String nickName, Pageable pageable, Long userId) {

        //작성자 닉네임에 의한 산책길 글목록 조회
        Page<Post> pagePostDTO = gilListRepository.findByNickName(nickName, pageable);
        List<Post> listPostDTO = pagePostDTO.getContent();

        return new PageImpl<>(
                this.changeList(listPostDTO, userId),
                pageable,
                listPostDTO.size()
        );
    }

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findMyGilList(Pageable pageable, Long userId) {

        //글쓴 유저 id에 의한 산책길 글목록 찾기
        Page<Post> pagePostDTO = gilListRepository.findByUserId(userId, pageable);

        List<Post> listPostDTO = pagePostDTO.getContent();

        return new PageImpl<>(
                this.changeList(listPostDTO, userId),
                pageable,
                listPostDTO.size()
        );
    }

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findMyFav(Pageable pageable, Long userId) {

        // 전체 산책길 글목록 조회 후 List에 저장
        List<Post> allPostsList = new ArrayList<>(gilListRepository.findAllPostDTO(Pageable.unpaged()).getContent());

        // 해당 유저 ID가 찜한 목록만 필터링
        List<Post> filteredPostsList = allPostsList.stream()
                .filter(post -> post.getPostWishLists().stream()
                        .anyMatch(postWishlist -> postWishlist.getUser().getId().equals(userId))) // 유저 ID가 있는지 확인
                .collect(Collectors.toList());

        //좋아요 순 정렬
        filteredPostsList.sort((p1, p2) -> Integer.compare(p2.getPostLikes().size(), p1.getPostLikes().size()));

        // 전체 데이터 수 계산
        long total = filteredPostsList.size();

        // 페이징 처리: 현재 페이지 범위에 해당하는 데이터 슬라이싱
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredPostsList.size());
        List<Post> pagedResult = filteredPostsList.subList(start, end);

        // 반환
        return new PageImpl<>(this.changeList(pagedResult, userId), pageable, total);
    }

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByKeyword(String keyword, Pageable pageable, Long userId) {
        //검색 조건에 맞는 postId들 조회
        List<String> postIds = elasticsearchService.multiFieldSearch(
                "post-index",
                keyword,
                List.of("title", "content", "startAddr", "nickName"),
                10000
        );
        if (postIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        //현재 페이지에 해당하는 postId들만 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), postIds.size());

        if (start >= postIds.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, postIds.size());
        }

        List<String> pagedPostIds = postIds.subList(start, end);

        // 현재 페이지의 postId들로 Post 조회
        List<Post> pagePosts = new ArrayList<>();
        for (String postId : pagedPostIds) {
            Page<Post> postPage = gilListRepository.findById(Long.parseLong(postId), PageRequest.of(0, 1));
            if (!postPage.isEmpty()) {
                pagePosts.addAll(postPage.getContent());
            }
        }
        pagePosts.sort((p1, p2) -> Integer.compare(p2.getPostLikes().size(), p1.getPostLikes().size()));

        List<PostResDTO> postResDTOs = this.changeList(pagePosts, userId);

        return new PageImpl<>(postResDTOs, pageable, postIds.size());
    }

    /**
     * 7. 태그 검색으로 글목록 조회하기
     * */
    @Transactional(readOnly = true)
    @Override
    public Page<PostResDTO> findByTag(String tag, Pageable pageable, Long userId) {

        Page<Post> pagePostDTO = gilListRepository.findByTag(tag, pageable);
        List<Post> listPostDTO = pagePostDTO.getContent();

        return new PageImpl<>(
                this.changeList(listPostDTO, userId),
                pageable,
                listPostDTO.size()
        );
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
     * List<Post>를 List<PostResDTO>로 바꾸는 함수
     * */
    private List<PostResDTO> changeList(List<Post> listPostDTO, Long userId){
        List<PostResDTO> result = new ArrayList<>();
        for(Post post : listPostDTO){
            Long id = post.getId();
            Long postUserId=post.getUser().getId();
            String userNickName = post.getUser().getNickName();
            Long pathId = post.getPath().getId();
            Double startLat = post.getPath().getStartLat();
            Double startLong = post.getPath().getStartLong();
            Integer state = post.getState();
            String title = post.getTitle();
            String content = post.getContent();
            String tag = post.getTag();
            LocalDateTime writeDate = post.getWriteDate();
            LocalDateTime updateDate = post.getUpdateDate();
            Integer readNum = post.getReadNum();
            Integer postLikesCount = post.getPostLikes().size();
            Integer repliesCount = post.getReplies().size();
            Integer postWishListsNum = post.getPostWishLists().size();
            String userImgUrl = post.getUser().getImageUrl();

            PathResDTO pathResDTO = pathService.decodingPath(post.getPath()); //path 형식 바꾸기

            List<String> imageUrls = post.getPostImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());

            boolean isLiked = post.getPostLikes()
                    .stream()
                    .anyMatch(like->like.getUser().getId().equals(userId));

            boolean isWishListed = post.getPostWishLists()
                    .stream()
                    .anyMatch(postWishList->postWishList.getUser().getId().equals(userId));

            result.add(new PostResDTO(id,postUserId, userNickName, pathId, startLat, startLong, state, title, content, tag,
                    writeDate,updateDate,readNum,postLikesCount, repliesCount,
                    postWishListsNum, userImgUrl, pathResDTO, imageUrls, isLiked, isWishListed));
        }
        return result;
    }
}