package com.example.tily.socialLogin;

public class SocialLoginResponse{
    public record UserInfoDto(Long id, String nickname){};

    public record TokenDTO(String token){};
}


