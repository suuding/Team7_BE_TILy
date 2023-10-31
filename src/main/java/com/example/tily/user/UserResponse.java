package com.example.tily.user;

import java.util.List;


public class UserResponse {
    public record CheckEmailCodeDTO(String email) {}

    public record LoginDTO(String accessToken) {}

    public record TokenDTO(String accessToken, String refreshToken) {}

    public record ViewGardensDTO(List<GardenDTO> gardens) {
        public record GardenDTO(String date, int value) {}
    }
}
