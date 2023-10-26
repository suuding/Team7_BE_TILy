package com.example.tily.user;

import com.example.tily._core.security.JWTProvider;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 이메일 중복 체크 (후 인증코드 전송)
    @PostMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestBody @Valid UserRequest.CheckEmailDTO requestDTO, Errors errors) {
        userService.checkEmail(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 인증코드 전송
    @PostMapping("/email/code")
    public ResponseEntity<?> sendEmailCode(@RequestBody @Valid UserRequest.SendEmailCodeDTO requestDTO, Errors errors) {
        userService.sendEmailCode(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 인증코드 확인
    @PostMapping("/email/code/check")
    public ResponseEntity<?> checkEmailCode(@RequestBody @Valid UserRequest.CheckEmailCodeDTO requestDTO, Errors errors) {
        UserResponse.CheckEmailCodeDTO responseDTO = userService.checkEmailCode(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinDTO requestDTO, Errors errors) {
        userService.join(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO requestDTO, Errors errors) {
        UserResponse.TokenDTO responseDTO = userService.login(requestDTO);
        ResponseCookie responseCookie = setRefreshTokenCookie(responseDTO.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiUtils.success(new UserResponse.LoginDTO(responseDTO.getAccessToken())));
    }

    // 비밀번호 재설정
    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestBody @Valid UserRequest.ChangePwdDTO requestDTO, Errors errors) {
        userService.changePassword(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue String refreshToken) {
        UserResponse.TokenDTO responseDTO = userService.refresh(refreshToken);
        ResponseCookie responseCookie = setRefreshTokenCookie(responseDTO.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(ApiUtils.success(new UserResponse.LoginDTO(responseDTO.getAccessToken())));
    }

}
