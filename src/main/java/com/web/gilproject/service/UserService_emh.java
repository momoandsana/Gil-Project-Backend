package com.web.gilproject.service;

import com.web.gilproject.domain.Subscribe;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.dto.UserSimpleResDTO;

import java.util.List;

public interface UserService_emh {

    /**
     * 내 정보 조회
     * @param userId
     * @return (User정보, Path정보, Post정보, Subscribe정보, Wishlist정보)
     */
    UserDTO findUserById(Long userId);

    /**
     * 프로필 눌렀을 때 다른 유저에게 보이는 내 정보 조회
     * (프로필 이미지, 닉네임, 자기소개글, 내가 쓴 글 개수, 구독자 수, 따라걷기 수)
     */
    UserSimpleResDTO findSimpleInfoById(Long userId, Long selectedUserId);

    /**
     * 내 프로필 이미지 수정
     * @param id
     * @param fileUrl
     */
    void updateUserImg(Long id, String fileUrl);

    /**
     * 내 정보 수정 (닉네임, 이메일, 자기소개글)
     * @param id
     * @param userDTO
     */
    void updateUserInfo(Long id, UserDTO userDTO);

    /**
     * 비밀번호 변경
     * @param id
     * @param password
     */
    void updateUserPassword(Long id, String password);

    /**
     * 내 주소 수정
     * @param id
     * @param userDTO
     */
    void updateUserAddr(Long id, UserDTO userDTO);


    /**
     * 나를 구독한 유저 목록 조회
     */
    List<UserSimpleResDTO> findAllSubscribeByUserId(Long userId);


    /**
     * 들어온 비밀번호와 유저의 비밀번호가 일치하는지 확인
     */
    Boolean matchUserPassword(Long userId, String password);

    /**
     * 닉네임 변경
     * @param id
     * @param nickname
     */
    void updateUserNickname(Long id, String nickname);
}
