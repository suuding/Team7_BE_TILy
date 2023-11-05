package com.example.tily.socialLogin;

public class SocialLoginResponse{
    public record UserInfoDto(String nickname, String email){};

    public record TokenDTO(String token){};
}


