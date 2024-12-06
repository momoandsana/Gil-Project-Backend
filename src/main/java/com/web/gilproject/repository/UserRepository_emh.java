package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository_emh extends JpaRepository<User, Long> {

    /**
     * 회원 주소 변경
     * @param id
     * @param address
     * @param longitude
     * @param latitude
     * @return
     */
    @Modifying
    @Query("update User u set u.address= :address, u.longitude =:longitude, u.latitude=:latitude where u.id=:id")
    int updateUserAddress(@Param("id") Long id,
                          @Param("address") String address,
                          @Param("longitude") Double longitude,
                          @Param("latitude") Double latitude);
}
