package com.example.tily.alarm;

import java.util.List;

public record AlarmRequest() {

    public record ReadAlarmDTO(List<AlarmDTO> alarms) {

        public record AlarmDTO(Long id) {
        }
    }
}
