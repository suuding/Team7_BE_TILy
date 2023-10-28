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
            private String date;
            private int value;

            public GardenDTO(String date, int value) {
                this.date = date;
                this.value = value;
            }
        }

    }
}
