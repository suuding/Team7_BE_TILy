package com.example.tily.til;

import com.example.tily.comment.Comment;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TilResponse {
    public record CreateTilDTO(Long id) {
        public CreateTilDTO(Til til) {
            this(til.getId());
        }
    }
    
    public record ViewDTO(String content,
                          String submitContent,
                          boolean isPersonal,
                          boolean isSubmit,
                          String roadmapName,
                          StepDTO step,
                          List<CommentDTO> comments) {
        public ViewDTO(Step step, Til til, boolean isSubmit, List<CommentDTO> comments) {
            this(til.getContent(),
                    til.getSubmitContent(),
                    til.isPersonal(),
                    isSubmit,
                    step.getRoadmap().getName(),
                    new StepDTO(step), comments);
        }

        public record StepDTO(Long id, String title) {
            public StepDTO(Step step) {
                this(step.getId(), step.getTitle());
            }
        }

        public record CommentDTO(Long id,
                                 String content,
                                 String name,
                                 String image,
                                 boolean isOwner,
                                 LocalDateTime createDate) {
            public CommentDTO(Comment comment, boolean isOwner) {
                this(comment.getId(),
                        comment.getContent(),
                        comment.getWriter().getName(),
                        comment.getWriter().getImage(),
                        isOwner,
                        comment.getCreatedDate());
            }
        }
    }

    public record FindAllDTO(List<TilDTO> tils, boolean hasNext) {}

    public record TilDTO(Long id,
                         String createDate,
                         StepDTO step,
                         RoadmapDTO roadmap) {
        public TilDTO(Til til, Step step, Roadmap roadmap) {
            this(til.getId(),
                    til.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    new StepDTO(step),
                    new RoadmapDTO(roadmap));
        }

        public record StepDTO(Long id, String title) {
            public StepDTO(Step step) {
                this(step.getId(), step.getTitle());
            }
        }

        public record RoadmapDTO(Long id, String name, String category) {
            public RoadmapDTO(Roadmap roadmap) {
                this(roadmap.getId(), roadmap.getName(), roadmap.getCategory().getValue());
            }
        }
    }
}