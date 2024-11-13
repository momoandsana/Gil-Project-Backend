package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.repository.UserRepository_JHW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    //데이터베이스 연결할 수 있는 리포지토리
    private final UserRepository_JHW userRepository;

    /**
     * 이메일로 인증(메소드 이름은 신경쓰지마세요)
     * @param email 회원 이메일
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userData = userRepository.findByEmail(email);

        if(userData !=null){
            return new CustomUserDetails(userData);
        }
        return null;
    }

}
