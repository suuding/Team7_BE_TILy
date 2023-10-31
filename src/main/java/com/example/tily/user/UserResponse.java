package com.example.tily.user;

import java.util.List;


public class UserResponse {
    public record CheckEmailCodeDTO(String email) {}

    public record LoginDTO(String accessToken) {}

    public record TokenDTO(String accessToken, String refreshToken) {}

    public record ViewGardensDTO(List<GardenDTO> gardens) {
        public record GardenDTO(String date, int value) {}
    }


<<<<<<< HEAD
=======
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
>>>>>>> upstream/weekly
}
