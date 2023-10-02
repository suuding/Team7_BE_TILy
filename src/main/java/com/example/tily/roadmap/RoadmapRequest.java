package com.example.tily.roadmap;

import com.example.tily.step.Step;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    public static class CreateGroupRoadmapDTO{
        private RoadmapDTO roadmap;
        private List<StepDTOs> steps;

        @Getter @Setter
        public class RoadmapDTO{
            private String name;
            private String description;
            private Boolean isPublic;
        }

        @Getter @Setter
        public class StepDTOs{
            private String title;
            private String description;
            private ReferenceDTO references;

            @Getter @Setter
            public class ReferenceDTO{
                List<Link> youtube; // request JSON 형식에 따라..
                List<Link> reference;

                @Getter @Setter
                public class Link {
                    private String link;
                }
            }
        }
    }
}
