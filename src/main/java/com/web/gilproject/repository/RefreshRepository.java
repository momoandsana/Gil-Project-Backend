package com.web.gilproject.repository;

import com.web.gilproject.domain.Refresh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    /**
     * 리프레시 토큰이 존재하는지 확인
     * @param refreshToken
     * @return
     */
    Boolean existsByRefreshToken(String refreshToken);

    /**
     * 리프레시 토큰 삭제
     * @param refreshToken
     */
    @Transactional
    void deleteByRefreshToken(String refreshToken);

    /**
     * 기한이 만료된 리프레시 토큰 삭제
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Refresh r WHERE r.expiration < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
