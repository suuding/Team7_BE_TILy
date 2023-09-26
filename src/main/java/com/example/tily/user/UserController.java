package com.example.tily.user;

import com.example.tily._core.security.JWTProvider;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinDTO requestDTO, Error errors) {
        userService.join(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 이메일 중복 체크
    @PostMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestBody @Valid UserRequest.EmailCheckDTO requestDTO, Error errors) {
        userService.checkEmail(requestDTO.getEmail());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

}
