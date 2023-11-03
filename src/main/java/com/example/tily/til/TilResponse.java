package com.example.tily.til;

import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentResponse;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Slice;

import javax.swing.text.StyledEditorKit;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TilResponse {
    public record CreateTilDTO(Long id) {
        public CreateTilDTO(Til til) {
            this(til.getId());
        }
    }

    public record ViewDTO(String content, Boolean isPersonal, Boolean isSubmit, String roadmapName, StepDTO step, List<CommentDTO> comments) {
        public ViewDTO(Step step, Til til, Boolean isSubmit, List<CommentDTO> comments) {
            this(til.getContent(), til.isPersonal(), isSubmit, step.getRoadmap().getName(), new StepDTO(step), comments);
        }

        public record StepDTO(Long id, String title) {
            public StepDTO(Step step) {
                this(step.getId(), step.getTitle());
            }
        }

        public record CommentDTO(Long id, String content, String name, String image, Boolean isOwner, String createDate) {
            public CommentDTO(Comment comment, Boolean isOwner) {
                this(comment.getId(), comment.getContent(), comment.getWriter().getName(), comment.getWriter().getImage(), isOwner,
                        comment.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
    }

    public record FindAllDTO(List<TilDTO> tils, Boolean hasNext) {}

    public record TilDTO(Long id, String createDate, StepDTO step, RoadmapDTO roadmap) {
        public TilDTO(Til til, Step step, Roadmap roadmap) {
            this(til.getId(), til.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), new StepDTO(step), new RoadmapDTO(roadmap));
        }

        public record StepDTO(Long id, String title) {
            public StepDTO(Step step) {
                this(step.getId(), step.getTitle());
            }
        }

        public record RoadmapDTO(Long id, String name) {
            public RoadmapDTO(Roadmap roadmap) {
                this(roadmap.getId(), roadmap.getName());
            }
        }
    }
}