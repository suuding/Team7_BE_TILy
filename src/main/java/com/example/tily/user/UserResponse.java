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
}
