package com.example.tily.alarm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AlarmRequest {

    @Getter @Setter
    public static class ReadAlarmDTO {

        private List<AlarmDTO> alarms;

        @Getter @Setter
        public static class AlarmDTO {
            private Long id;
        }

    }
}
