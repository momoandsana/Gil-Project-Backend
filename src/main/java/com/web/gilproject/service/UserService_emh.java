package com.web.gilproject.service;

import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.dto.UserSimpleResDTO;

public interface UserService_emh {

    /**
     * 내 정보 조회
     * @param userId
     * @return (User정보, Path정보, Post정보, Subscribe정보, Wishlist정보)
     */
    UserDTO findUserById(Long userId);


    /**
     * 내 정보 수정
     * @param id
     * @param userDTO
     */
    void updateUserInfo(Long id, UserDTO userDTO);

    /**
     * 내 주소 수정
     * @param id
     * @param userDTO
     */
    void updateUserAddr(Long id, UserDTO userDTO);

    /**
     * 내 프로필 수정
     */
    void updateUserImg(Long id, String fileUrl);


    /**
     * 내 게시물 보기
     */

    /**
     * 프로필 눌렀을 때 다른 유저에게 보이는 내 정보 조회
     * (프로필 이미지, 닉네임, 자기소개글, 내가 쓴 글 개수, 구독자 수, 따라걷기 수)
     */
    UserSimpleResDTO findSimpleInfoById(Long id);

}
