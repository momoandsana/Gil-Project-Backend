package com.web.gilproject.service;

import com.web.gilproject.dto.UserDTO;

public interface UserService_emh {

    /**
     * 내 정보 조회
     * @param id
     * @return (User정보, Path정보, Post정보, Subscribe정보, Wishlist정보)
     */
    UserDTO findUserById(Long id);


    /**
     * 내 정보 수정
     * @param id
     * @param userDTO
     */
    void updateUserInfo(Long id, UserDTO userDTO);

    /**
     * 내 프로필 수정
     */
    void updateUserImg(Long id, String fileUrl);
}
