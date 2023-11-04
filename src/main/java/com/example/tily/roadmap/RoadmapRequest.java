package com.example.tily.roadmap;

import com.example.tily.roadmap.relation.GroupRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class RoadmapRequest {

    public record CreateIndividualRoadmapDTO(@NotBlank(message = "이름을 입력해주세요.")
                                             @Size(min=2, max=20, message = "이름은 2자에서 20자 이내여야 합니다.") String name) {
    }

    public record CreateGroupRoadmapDTO(RoadmapDTO roadmap, List<StepDTO> steps) {
    }

    public record UpdateGroupRoadmapDTO(RoadmapDTO roadmap, List<StepDTO> steps) {
    }

    public record RoadmapDTO(@NotBlank(message = "이름을 입력해주세요.") String name, String description, String code, Boolean isPublic, Boolean isRecruit){
    }

    public record StepDTO(
            Long id,
            @NotBlank(message = "제목을 입력해주세요.")
            String title,
            String description,
            ReferenceDTOs references,
            LocalDateTime dueDate) {}

    public record ReferenceDTOs(List<ReferenceDTO> youtube, List<ReferenceDTO> web) {
    }

    public record ReferenceDTO(Long id, @NotBlank(message = "링크 주소를 입력해주세요.") String link) {
    }

    public record ApplyRoadmapDTO(@NotBlank(message="소개를 입력해주세요.")String content){
    }

    public record ParticipateRoadmapDTO(@NotBlank(message="이름을 입력해주세요.")
                                        @Size(min=8, max=8, message = "코드는 8자여야 합니다.") String code){
    }

    public record ChangeMemberRoleDTO(@NotNull(message="역할을 선택해주세요.") String role){
    }
}


