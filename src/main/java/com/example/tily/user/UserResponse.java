package com.example.tily.user;

import java.util.List;


public class UserResponse {
    public record CheckEmailCodeDTO(String email) {}

    public record LoginDTO(String accessToken) {}

    public record TokenDTO(String accessToken, String refreshToken) {}

    public record ViewGardensDTO(List<GardenDTO> gardens) {
        public record GardenDTO(String day, int value) {}
    }

    public record UserDTO(Long id, String name, String email, String image) {
        public UserDTO(User user) {
            this(user.getId(), user.getName(), user.getEmail(), user.getImage());
        }
    }
}
