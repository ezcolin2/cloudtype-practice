package com.example.auth.controller;

import com.example.auth.domain.User;
import com.example.auth.domain.dto.UserLoginRequest;
import com.example.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody User dto) {
        userService.join(dto.getUserName(), dto.getUserPwd());
        return ResponseEntity.ok().body("회원가입");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User dto) {
        String token = userService.login(dto.getUserName(), dto.getUserPwd());
        return ResponseEntity.ok().body(token);
    }
}
