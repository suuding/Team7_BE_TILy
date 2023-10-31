package com.example.tily.user;

import com.example.tily.til.Til;
import com.example.tily.til.TilResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UserResponse {
    public record CheckEmailCodeDTO(String email) {}

    public record LoginDTO(String accessToken) {}

    public record TokenDTO(String accessToken, String refreshToken) {}

    public record ViewGardensDTO(List<GardenDTO> gardens) {
        public record GardenDTO(String date, int value) {}
    }
}
