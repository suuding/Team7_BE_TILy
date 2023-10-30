package com.example.tily.roadmap;

import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.step.Step;
import com.example.tily.step.reference.Reference;
import com.example.tily.til.Til;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Slice;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoadmapResponse {
    @Getter @Setter
    public static class CreateRoadmapDTO {
        private Long id;

        public CreateRoadmapDTO(Roadmap roadmap)
        {
            this.id = roadmap.getId();
        }
    }

    @Getter @Setter
    public static class FindGroupRoadmapDTO {
        private Creator creator;
        private String name;
        private String description;
        private Role role;
        private Long recentTilId;
        private String code;
        private List<StepDTO> steps;

        public FindGroupRoadmapDTO(Roadmap roadmap, List<Step> stepList, Map<Long, List<Reference>> youtubeMap, Map<Long, List<Reference>> webMap, User user, Long recentTilId){
            this.creator = new Creator(user.getName(), user.getImage());
            this.name = roadmap.getName();
            this.description = roadmap.getDescription();
            this.role = user.getRole();
            this.recentTilId = recentTilId;
            this.code = roadmap.getCode();
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

    public record FindAllMyRoadmapDTO(List<CategoryDTO> categories, RoadmapDTO roadmaps) {

        public record CategoryDTO(Long id, String name) {
            public CategoryDTO(Roadmap roadmap) {
                this(roadmap.getId(), roadmap.getName());
            }
        }

       public record RoadmapDTO(List<TilyDTO> tilys, List<GroupDTO> groups) {}
    }

    @Getter @Setter
    public static class GroupDTO {
        private Long id;
        private String name;
        private int stepNum;
        private Creator creator;

        public GroupDTO(Roadmap roadmap) {
            this.id = roadmap.getId();
            this.name = roadmap.getName();
            this.stepNum = roadmap.getStepNum();
            this.creator = new Creator(roadmap.getCreator());
        }

        @Getter @Setter
        public static class Creator {
            private Long id;
            private String name;
            private String image;

            public Creator(User user) {
                this.id = user.getId();
                this.name = user.getName();
                this.image = user.getImage();
            }
        }
    }

    @Getter @Setter
    public static class TilyDTO {
        private Long id;
        private String name;
        private String image;
        private int stepNum;

        public TilyDTO(Roadmap roadmap) {
            this.id = roadmap.getId();
            this.name = roadmap.getName();
            this.image = roadmap.getImage();
            this.stepNum = roadmap.getStepNum();
        }
    }

    @Getter @Setter
    public static class FindRoadmapByQueryDTO {
        private String category;
        private List<RoadmapDTO> roadmaps;
        private Boolean hasNext;

        public FindRoadmapByQueryDTO(Category category, Slice<Roadmap> roadmaps) {
            this.category = category.getValue();
            this.roadmaps = roadmaps.getContent().stream().map(RoadmapDTO::new).collect(Collectors.toList());
            this.hasNext = roadmaps.hasNext();
        }

        @Getter @Setter
        public class RoadmapDTO {
            private Long id;
            private String name;
            private int stepNum;
            private FindAllMyRoadmapDTO.RoadmapDTO.GroupDTO.Creator creator;

            public RoadmapDTO(Roadmap roadmap) {
                this.id = roadmap.getId();
                this.name = roadmap.getName();
                this.stepNum = roadmap.getStepNum();
                this.creator = new FindAllMyRoadmapDTO.RoadmapDTO.GroupDTO.Creator(roadmap.getCreator());
            }
        }
    }

    @Getter @Setter
    public static class ParticipateRoadmapDTO{
        private Long id;

        public ParticipateRoadmapDTO(Roadmap roadmap){ this.id = roadmap.getId(); }
    }

    public record FindRoadmapMembersDTO(List<UserDTO> users) {
        public record UserDTO(Long id, String name, String image, GroupRole role) {}
    }

    public record FindAppliedUsersDTO(List<UserDTO> users, GroupRole myRole) {
        public record UserDTO(Long id, String name, String image, LocalDate date, String content) {}
    }

    public record FindTilOfStepDTO(List<MemberDTO> members) {
        public record MemberDTO(Long tilId, Long userId, String name, String image,
                                String content, LocalDate submitDate, int commentNum) {}
    }
}
