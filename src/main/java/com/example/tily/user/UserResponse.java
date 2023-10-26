package com.example.tily.user;

import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter @Setter
    public static class CheckEmailCodeDTO {
        private String email;

        public CheckEmailCodeDTO(String email) {
            this.email = email;
        }
    }

    @Getter @Setter
    public static class LoginDTO {
        private String accessToken;
        public LoginDTO(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    @Getter @Setter
    public static class TokenDTO {
        private String accessToken;
        private String refreshToken;
        public TokenDTO(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
