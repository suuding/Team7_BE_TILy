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

    @Getter @Setter
    public static class CreateTilDTO {
        private Long id;
        public CreateTilDTO(Til til){
            this.id = til.getId();
        }
    }

    @Getter @Setter
    public static class ViewDTO {
        private String content;
        private Boolean isPersonal;
        private Boolean isCompleted;
        private StepDTO step;
        private List<CommentDTO> comments;

        public ViewDTO(Step step, Til til, Boolean isCompleted, List<Comment> comments, Map<Comment, Boolean> maps) {
            this.content = til.getContent();
            this.isPersonal = til.isPersonal();
            this.isCompleted = isCompleted;
            this.step = new StepDTO(step);
            this.comments = comments.stream().map(c -> new CommentDTO(c, maps.get(c))).collect(Collectors.toList());
        }

        @Getter @Setter
        public class StepDTO {
            private Long id;
            private String title;
            public StepDTO(Step step) {
                this.id = step.getId();
                this.title = step.getTitle();
            }
        }
        @Getter @Setter
        public class CommentDTO {
            private Long id;
            private String content;
            private String name;
            private String image;
            private Boolean isOwner;
            private String createDate;

            public CommentDTO(Comment comment, Boolean isOwner){
                this.id = comment.getId();
                this.content = comment.getContent();
                this.name = comment.getWriter().getName();
                this.image = comment.getWriter().getImage();
                this.isOwner = isOwner;
                this.createDate = comment.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

        }

    }

    @Getter
    @Setter
    public static class FindAllDTO {
        private List<TilDTO> tils;
        private Boolean hasNext;

        public FindAllDTO(Slice<Til> tils) {
            this.tils = tils.getContent().stream()
                    .map(til -> new TilDTO(til, til.getStep(), til.getRoadmap()))
                    .collect(Collectors.toList());
            this.hasNext = tils.hasNext();
        }

        @Getter
        @Setter
        public class TilDTO {
            private Long id;
            private String createDate;
            private StepDTO step;
            private RoadmapDTO roadmap;

            public TilDTO(Til til, Step step, Roadmap roadmap) {
                this.id = til.getId();
                this.createDate = til.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                this.step = new StepDTO(step);
                this.roadmap = new RoadmapDTO(roadmap);
            }

            @Getter
            @Setter
            public class StepDTO {
                private Long id;
                private String title;

                public StepDTO(Step step) {
                    this.id = step.getId();
                    this.title = step.getTitle();
                }
            }

            @Getter
            @Setter
            public class RoadmapDTO {
                private Long id;
                private String name;

                public RoadmapDTO(Roadmap roadmap) {
                    this.id = roadmap.getId();
                    this.name = roadmap.getName();
                }
            }
        }
    }
}