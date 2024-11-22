package com.web.gilproject.service;

import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GilListService {
    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    Page<PostResDTO> findByMyPosition(Double nowY, Double nowX, Pageable pageable, Long userId);

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    Page<PostResDTO> findByNearAddr(Pageable pageable, Long userId);

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    Page<PostResDTO> findByNickName(String nickName, Pageable pageable, Long userId);

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    Page<PostResDTO> findMyGilList(Pageable pageable, Long userId);

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    Page<PostResDTO> findMyFav(Pageable pageable, Long userId);

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    Page<PostResDTO> findByKeyword(String keyword, Pageable pageable, Long userId);
}
