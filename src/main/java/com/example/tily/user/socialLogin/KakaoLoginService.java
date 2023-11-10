package com.example.tily.user.socialLogin;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.security.JWTProvider;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import com.example.tily.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SocialLoginResponse.TokenDTO kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        SocialLoginResponse.UserInfoDto userInfo = getUserInfo(accessToken);

        // 3. 카카오ID로 회원가입 처리
        User kakaoUser = registerUser(userInfo);

        // 4. 로그인 처리
        Authentication authentication = forceLogin(kakaoUser);

        // 5. JWT 토큰을 응답에 넣음
        SocialLoginResponse.TokenDTO response = getJwtToken(authentication);

        return response;
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "872b661d1b5d025d01a76fdb6936f3fb");
        body.add("redirect_uri", "https://kc29be941feb6a.user-app.krampoline.com/auth/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        String response = sendRequest("https://kauth.kakao.com/oauth/token", HttpMethod.POST, body, headers);

        // 액세스 토큰 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        return jsonNode.get("access_token").asText();
    }

    private SocialLoginResponse.UserInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // Bearer을 빼면 작동 할 수도 있음
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        String response = sendRequest("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, null, headers);

        // responseBody에 있는 정보를 꺼냄
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        return new SocialLoginResponse.UserInfoDto(nickname, email);
    }

    private User registerUser(SocialLoginResponse.UserInfoDto kakaoUserInfo) {
        // 이미 가입한 회원인지 확인
        String nickname = kakaoUserInfo.nickname();
        String kakaoEmail = kakaoUserInfo.email();
        User kakaoUser = userRepository.findByEmail(kakaoEmail).orElse(null);

        if (kakaoUser == null) {
            // 회원가입
            String password = UUID.randomUUID().toString(); // 비밀번호는 랜던
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = User.builder()
                    .email(kakaoEmail)
                    .name(nickname)
                    .password(encodedPassword)
                    .role(Role.ROLE_USER)
                    .build();

            userRepository.save(kakaoUser);
        }

        return kakaoUser;
    }

    private Authentication forceLogin(User kakaoUser) {
        UserDetails userDetails = new CustomUserDetails(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 현재 실행 중인 스레드에 인증 정보를 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private SocialLoginResponse.TokenDTO getJwtToken(Authentication authentication) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        String token = JWTProvider.createAccessToken(customUserDetails.getUser());

        return new SocialLoginResponse.TokenDTO(token);
    }

    private String sendRequest(String url, HttpMethod method, MultiValueMap<String, String> body, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        //ResponseEntity<String> response = rt.exchange(url, method, request, String.class);
        ResponseEntity<String> response = rt.postForEntity(url, request, String.class);

        return response.getBody();
    }
}