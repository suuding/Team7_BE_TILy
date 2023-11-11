package com.example.tily.alarm;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.user.User;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlarmResponse {

    public record RoadmapDTO(
            Long id,
            String name
    ) {
        public RoadmapDTO(Roadmap roadmap) {
            this(roadmap.getId(),
                 roadmap.getName());
        }
    }

    public record StepDTO(
            Long id,
            String title
    ){
        public StepDTO(Step step) {
            this(step.getId(),
                 step.getTitle());
        }
    }

    public record SenderDTO(
            String name,
            String image
    ){
        public SenderDTO(User user) {
            this(user.getName(),
                 user.getImage());
        }
    }

    public record AlarmDTO(Long id,
                           Long tilId,
                           boolean isRead,
                           String createDate,
                           RoadmapDTO roadmap,
                           StepDTO step,
                           SenderDTO sender){

        public AlarmDTO(Alarm alarm){
            this(alarm.getId(),
                    alarm.getTil().getId(),
                    alarm.isRead(),
                    alarm.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    new RoadmapDTO(alarm.getTil().getRoadmap()),
                    new StepDTO(alarm.getTil().getStep()),
                    new SenderDTO(alarm.getComment().getWriter()));
        }
    }

    public record FindAllDTO(List<AlarmDTO> alarms)
    { }
}
