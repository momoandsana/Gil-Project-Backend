package com.web.gilproject.service;

import com.web.gilproject.dto.PostDTO_YJ.PostDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface GilListService {
    /**
     * 1. 내 위치 주변 산책길 글목록
     * */
    List<PostDTO> findByMyPosition(Double nowY, Double nowX);

    /**
     * 2. 내 주소 주변 산책길 글목록
     * */
    List<PostDTO> findByNearAddr(Authentication authentication);

    /**
     * 3. (구독기능을 위한) 작성자별 산책길 글목록
     * */
    List<PostDTO> findByNickName(String nickName);

    /**
     * 4. 내가 쓴 산책길 글목록
     * */
    List<PostDTO> findMyGilList(Authentication authentication);

    /**
     * 5. 내가 찜한 산책길 글목록
     * */
    List<PostDTO> findMyFav(Authentication authentication);

    /**
     * 6. 키워드 검색으로 글목록 조회하기
     * */
    List<PostDTO> findByKeyword(String keyword);
}
