package com.example.tily.SocialLogin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class SocialLoginController {
    private final KakaoLoginService kakaoLoginService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public SocialUserInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoLoginService.kakaoLogin(code, response);
    }
}