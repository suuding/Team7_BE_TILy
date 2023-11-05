package com.example.tily.socialLogin;

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

import javax.servlet.http.HttpServletResponse;
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

        // 5. response Header에 JWT 토큰 추가
        SocialLoginResponse.TokenDTO response = putJwtToken(authentication);
        return response;
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "82e7687da8c2542f0d59deb7b6cc79ad");
        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
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
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        String response = sendRequest("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, null, headers);

        // responseBody에 있는 정보를 꺼냄
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        return new SocialLoginResponse.UserInfoDto(id, nickname, email);
    }

    private User registerUser(SocialLoginResponse.UserInfoDto kakaoUserInfo) {
        // DB 에 중복된 email이 있는지 확인
        String kakaoEmail = kakaoUserInfo.email();
        String nickname = kakaoUserInfo.nickname();
        User kakaoUser = userRepository.findByEmail(kakaoEmail)
                .orElse(null);

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

        // SecurityContextHolder를 통해 현재 스레드에 인증 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private SocialLoginResponse.TokenDTO putJwtToken(Authentication authentication) {
        // response header에 token 추가
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        String token = JWTProvider.createAccessToken(customUserDetails.getUser());

        return new SocialLoginResponse.TokenDTO(token);
    }

    private String sendRequest(String url, HttpMethod method, MultiValueMap<String, String> body, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(url, method, request, String.class);
        return response.getBody();
    }
}