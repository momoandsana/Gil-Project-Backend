package com.web.gilproject.service;

import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface GilListService {
    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    Page<PostResDTO> findByMyPosition(Double nowY, Double nowX, Pageable pageable);

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    Page<PostResDTO> findByNearAddr(Authentication authentication, Pageable pageable);

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    Page<PostResDTO> findByNickName(String nickName, Pageable pageable);

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    Page<PostResDTO> findMyGilList(Authentication authentication, Pageable pageable);

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    Page<PostResDTO> findMyFav(Authentication authentication, Pageable pageable);

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    Page<PostResDTO> findByKeyword(String keyword, Pageable pageable);
}
