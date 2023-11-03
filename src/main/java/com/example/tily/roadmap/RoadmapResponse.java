package com.example.tily.roadmap;

import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.step.Step;
import com.example.tily.step.reference.Reference;
import com.example.tily.til.Til;
import com.example.tily.user.Role;
import com.example.tily.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class RoadmapResponse {
    public record CreateRoadmapDTO(Long id) {
        public CreateRoadmapDTO(Roadmap roadmap) {
            this(roadmap.getId());
        }
    }

    public record FindGroupRoadmapDTO(Creator creator, String name, String description, String myRole, Long recentTilId, Long recentStepId, Boolean isPublic, Boolean isRecruit, String code, List<StepDTO> steps) {
        public FindGroupRoadmapDTO(Roadmap roadmap, List<StepDTO> steps, User user, Long recentTilId, Long recentStepId, String myRole) {
            this(new Creator(user.getName(), user.getImage()), roadmap.getName(), roadmap.getDescription(), myRole, recentTilId, recentStepId, roadmap.getIsPublic(), roadmap.getIsRecruit(), roadmap.getCode(), steps);
        }

        public record Creator(String name, String image) {}

        public record StepDTO(Long id, String title, String description, String dueDate, ReferenceDTOs references) {

            public StepDTO(Step step, List<ReferenceDTOs.ReferenceDTO> youtubeList, List<ReferenceDTOs.ReferenceDTO> webList) {
                this(step.getId(), step.getTitle(), step.getDescription(), step.getDueDate()!=null ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(step.getDueDate()) : null, new ReferenceDTOs(youtubeList, webList));
            }
        }
    }

    public record ReferenceDTOs(List<ReferenceDTO> youtube, List<ReferenceDTO> web) {
        public record ReferenceDTO(Long id, String link) {
            public ReferenceDTO(Reference reference) {
                this(reference.getId(), reference.getLink());
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

    public record TilyDTO (Long id, String name, String image, int stepNum) {
        public TilyDTO(Roadmap roadmap) {
            this(roadmap.getId(), roadmap.getName(), roadmap.getImage(), roadmap.getStepNum());
        }
    }

    public record GroupDTO (Long id, String name, int stepNum, Creator creator, boolean isManager) {
        public GroupDTO(Roadmap roadmap, Boolean isManager) {
            this(roadmap.getId(), roadmap.getName(), roadmap.getStepNum(), new Creator(roadmap.getCreator()), isManager);
        }

        public record Creator(Long id, String name, String image) {
            public Creator(User user) {
                this(user.getId(), user.getName(), user.getImage());
            }
        }
    }

    public record FindRoadmapByQueryDTO (String category, List<RoadmapDTO> roadmaps, Boolean hasNext) {
        public FindRoadmapByQueryDTO(Category category, List<RoadmapDTO> roadmaps, boolean hasNext) {
            this(category.getValue(), roadmaps, hasNext);
        }

        public record RoadmapDTO (Long id, String name, int stepNum, GroupDTO.Creator creator) {
            public RoadmapDTO(Roadmap roadmap) {
                this(roadmap.getId(), roadmap.getName(), roadmap.getStepNum(), new GroupDTO.Creator(roadmap.getCreator()));
            }
        }
    }

    public record ParticipateRoadmapDTO(Long id) {
        public ParticipateRoadmapDTO(Roadmap roadmap) {
            this(roadmap.getId());
        }
    }

    public record FindRoadmapMembersDTO(List<UserDTO> users) {
        public record UserDTO(Long id, String name, String image, String role) {}
    }

    public record FindAppliedUsersDTO(List<UserDTO> users) {
        public record UserDTO(Long id, String name, String image, LocalDate date, String content) {
            public UserDTO(User user, UserRoadmap userRoadmap) {
                this(user.getId(), user.getName(), user.getImage(), userRoadmap.getCreatedDate().toLocalDate(), userRoadmap.getContent());
            }
        }
    }

    public record FindTilOfStepDTO(List<MemberDTO> members) {
        public record MemberDTO(Long tilId, Long userId, String name, String image, String content, String submitDate, Integer commentNum) {

            public MemberDTO(Til til, User user) {
                this(
                        til!=null ? til.getId() : null,
                        user.getId(),
                        user.getName(),
                        user.getImage(),
                        til!=null ? til.getContent() : null,
                        til!=null ? til.getSubmitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null,
                        til!=null ? til.getCommentNum() : null);
            }
        }
    }
}
