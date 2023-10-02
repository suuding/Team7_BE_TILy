package com.example.tily.roadmap;

import com.example.tily.step.Step;
import com.example.tily.step.reference.Reference;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoadmapResponse {
    @Getter @Setter
    public static class CreateIndividualRoadmapDTO {
        private Long id;

        public CreateIndividualRoadmapDTO(Roadmap roadmap)
        {
            this.id = roadmap.getId();
        }
    }

    @Getter @Setter
    public static class createGroupRoadmapDTO{
        private Long id;

        public createGroupRoadmapDTO(Roadmap roadmap){ this.id = roadmap.getId();}
    }

    @Getter @Setter
    public static class findGroupRoadmapDTO{
        private Creator creator;
        private String name;
        private String description;
        private Role role;
        private Long recentTilId;
        private String code;
        private List<StepDTO> steps;

        public findGroupRoadmapDTO(Roadmap roadmap, List<Step> stepList, Map<Long, List<Reference>> youtubeMap, Map<Long, List<Reference>> webMap, User user, Long recentTilId){
            this.creator = new Creator(user.getName(), user.getImage());
            this.name = roadmap.getName();
            this.description = roadmap.getDescription();
            this.role = user.getRole();
            this.recentTilId = recentTilId;
            this.code = roadmap.getName();
            this.steps = stepList.stream()
                    .map(step -> new StepDTO(step, youtubeMap.get(step.getId()), webMap.get(step.getId())))
                    .collect(Collectors.toList());
        }

        @Getter @Setter
        public class Creator{
            private String name;
            private String image;

            public Creator(String name, String image){
                this.name = name;
                this.image = image;
            }
        }

        @Getter @Setter
        public class StepDTO{
            private Long id;
            private String title;
            private String description;
            private ReferenceDTOs references;

            public StepDTO(Step step, List<Reference> youtubeList, List<Reference> webList){
                this.id = step.getId();
                this.title = step.getTitle();
                this.description = step.getDescription();
                this.references = new ReferenceDTOs(youtubeList, webList);
            }

            @Getter @Setter
            public class ReferenceDTOs{
                List<ReferenceDTO> youtube;
                List<ReferenceDTO> web;

                public ReferenceDTOs(List<Reference> youtubeList, List<Reference> webList){
                    this.youtube = youtubeList.stream()
                            .map(reference -> new ReferenceDTO(reference))
                            .collect(Collectors.toList());
                    this.web = webList.stream()
                            .map(reference -> new ReferenceDTO(reference))
                            .collect(Collectors.toList());
                }

                @Getter @Setter
                public class ReferenceDTO {
                    private Long id;
                    private String link;

                    public ReferenceDTO(Reference reference){
                        this.id = reference.getId();
                        this.link = reference.getLink();
                    }
                }
            }
        }
    }
}
