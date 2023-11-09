package com.example.tily.user.socialLogin;

public class SocialLoginResponse{
    public record UserInfoDto(String nickname, String email){};

    public record TokenDTO(String accessToken){};
}


