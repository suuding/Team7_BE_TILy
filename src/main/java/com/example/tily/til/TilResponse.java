package com.example.tily.til;

import lombok.Getter;
import lombok.Setter;

public class TilResponse {

    @Getter @Setter
    public static class CreateTilDTO {
        private Long id;
        public CreateTilDTO(Til til){
            this.id = til.getId();
        }
    }
}
