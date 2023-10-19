package com.example.tily.alarm;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.til.TilResponse;
import com.example.tily.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AlarmResponse {

    @Getter @Setter
    public static class FindAllDTO {
        private List<AlarmDTO> alarms;

        public FindAllDTO(List<Alarm> alarms) {
            this.alarms = alarms.stream().map(alarm -> new AlarmDTO(alarm)).collect(Collectors.toList());
        }

        @Getter @Setter
        public class AlarmDTO {
            private Long id;
            private Long tilId;
            private Boolean isChecked;
            private String createDate;
            private RoadmapDTO roadmap;
            private StepDTO step;
            private SenderDTO sender;

            public AlarmDTO(Alarm alarm) {
                this.id = alarm.getId();
                this.tilId = alarm.getTil().getId();
                this.isChecked=alarm.getIsChecked();
                this.createDate = alarm.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                this.roadmap = new RoadmapDTO(alarm.getTil().getRoadmap());
                this.step = new StepDTO(alarm.getTil().getStep()) ;
                this.sender = new SenderDTO(alarm.getComment().getWriter());
            }

            @Getter @Setter
            public class RoadmapDTO {
                private Long id;
                private String name;

                public RoadmapDTO(Roadmap roadmap) {
                    this.id = roadmap.getId();
                    this.name = roadmap.getName();
                }
            }

            @Getter @Setter
            public class StepDTO {
                private Long id;
                private String title;

                public StepDTO(Step step) {
                    this.id = step.getId();
                    this.title = step.getTitle();
                }
            }

            @Getter @Setter
            public class SenderDTO {
                private String name;
                private String image;

                public SenderDTO(User user) {
                    this.name = user.getName();
                    this.image = user.getImage();
                }
            }
        }
    }
}
