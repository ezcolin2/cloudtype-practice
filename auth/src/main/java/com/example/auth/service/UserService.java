package com.example.auth.service;

import com.example.auth.domain.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiredMs}")
    private Long expiredMs;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public String join(String userName, String userPwd) {
        Optional<User> user = userRepository.findByUserName(userName);
        user.ifPresent(use->{
            throw new RuntimeException("중복");
            //발생하면 RestControllerAdvice 어노테이션 달린 곳으로 이동하는 듯?

        });
        User newUser=User.builder()
                        .userName(userName)
                        .userPwd(bCryptPasswordEncoder.encode(userPwd))
                                .build();
        userRepository.save(newUser);
        return "success";
    }

    public String login(String userName, String userPwd){
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isEmpty()){
            throw new RuntimeException("못 찾음");
        }
        if (bCryptPasswordEncoder.matches(userPwd, user.get().getUserPwd())){
            String jwt = JwtUtil.createJwt(userName, secretKey, expiredMs);
            return jwt;

        }
        else{
            throw new RuntimeException("비밀 번호 틀림 ");
        }
    }
}
