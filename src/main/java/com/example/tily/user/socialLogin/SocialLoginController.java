package com.example.tily.user.socialLogin;

import com.example.tily._core.utils.ApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SocialLoginController {
    private final KakaoLoginService kakaoLoginService;

    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        SocialLoginResponse.TokenDTO responseDTO = kakaoLoginService.kakaoLogin(code);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }
}