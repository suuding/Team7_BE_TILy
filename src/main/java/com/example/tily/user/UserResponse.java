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

    @Getter @Setter
    public static class ViewGardensDTO {
        private List<GardenDTO> gardens;
        public ViewGardensDTO(HashMap<String, Integer> maps){
            this.gardens = maps.entrySet().stream()
                    .map(garden -> new GardenDTO(garden.getKey(),garden.getValue()))
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public static class GardenDTO {
            private String day;
            private int value;

            public GardenDTO(String day, int value) {
                this.day = day;
                this.value = value;
            }
        }
    }

    @Getter @Setter
    public static class UserDTO {
        private Long id;
        private String name;
        private String email;
        private String image;

        public UserDTO(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.image = user.getImage();
        }
    }
}
