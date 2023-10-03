package com.example.tily.roadmap;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class RoadmapRequest {

    @Getter @Setter
    public static class CreateIndividualRoadmapDTO {
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(min=2, max=20, message = "이름은 2자에서 20자 이내여야 합니다.")
        private String name;
    }

    @Getter @Setter
    public static class CreateGroupRoadmapDTO {
        private RoadmapDTO roadmap;
        private List<StepDTO> steps;

        @Getter @Setter
        public class RoadmapDTO{
            private String name;
            private String description;
            private Boolean isPublic;
        }

        @Getter @Setter
        public class StepDTO{
            private String title;
            private String description;
            private ReferenceDTOs references;

            @Getter @Setter
            public class ReferenceDTOs {
                List<ReferenceDTO> youtube; // request JSON 형식에 따라..
                List<ReferenceDTO> web;

                @Getter @Setter
                public class ReferenceDTO {
                    private String link;
                }
            }
        }
    }

    @Getter @Setter
    public static class UpdateGroupRoadmapDTO {
        private RoadmapDTO roadmap;
        private List<StepDTO> steps;

        @Getter @Setter
        public class RoadmapDTO{
            private String name;
            private String description;
            private String code;
            private Boolean isPublic;
            private Boolean isRecruit;
        }

        @Getter @Setter
        public class StepDTO{
            private Long id;
            private String title;
            private String description;
            private ReferenceDTOs references;

            @Getter @Setter
            public class ReferenceDTOs {
                List<ReferenceDTO> youtube;
                List<ReferenceDTO> web;

                @Getter @Setter
                public class ReferenceDTO {
                    private Long id;
                    private String link;
                }
            }
        }
    }
}
