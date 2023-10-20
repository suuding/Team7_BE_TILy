package com.example.tily.roadmap;

import com.example.tily.roadmap.relation.GroupRole;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
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
        public static class RoadmapDTO{
            @NotBlank(message = "이름을 입력해주세요.")
            private String name;
            private String description;
            private Boolean isPublic;
        }

        @Getter @Setter
        public static class StepDTO{
            @NotBlank(message = "제목을 입력해주세요.")
            private String title;
            private String description;
            private ReferenceDTOs references;
            private LocalDateTime dueDate;

            @Getter @Setter
            public static class ReferenceDTOs {
                List<ReferenceDTO> youtube; // request JSON 형식에 따라..
                List<ReferenceDTO> web;

                @Getter @Setter
                public static class ReferenceDTO {
                    @NotBlank(message = "링크 주소를 입력해주세요.")
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
        public static class RoadmapDTO{
            @NotBlank(message = "이름을 입력해주세요.")
            private String name;
            private String description;
            private String code;
            private Boolean isPublic;
            private Boolean isRecruit;
        }

        @Getter @Setter
        public static class StepDTO{
            private Long id;
            @NotBlank(message = "제목을 입력해주세요.")
            private String title;
            private String description;
            private ReferenceDTOs references;

            @Getter @Setter
            public static class ReferenceDTOs {
                List<ReferenceDTO> youtube;
                List<ReferenceDTO> web;

                @Getter @Setter
                public static class ReferenceDTO {
                    private Long id;
                    @NotBlank(message = "링크 주소를 입력해주세요.")
                    private String link;
                }
            }
        }
    }

    @Getter @Setter
    public static class ApplyRoadmapDTO{
        @NotBlank(message="소개를 입력해주세요.")
        private String content;
    }

    @Getter @Setter
    public static class ParticipateRoadmapDTO{
        @NotBlank(message="이름을 입력해주세요.")
        @Size(min=8, max=8, message = "코드는 8자여야 합니다.")
        private String code;
    }

    @Getter @Setter
    public static class ChangeMemberRoleDTO{
        @NotNull(message="역할을 선택해주세요.")
        private GroupRole role;
    }
}
