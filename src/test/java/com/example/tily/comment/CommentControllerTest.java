package com.example.tily.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("댓글 생성 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void create_comment_success_test() throws Exception {

        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 1L;

        String content = "열심히 하셨군요 홍홍";
        CommentRequest.CreateCommentDTO requestDTO = new CommentRequest.CreateCommentDTO(roadmapId, stepId, tilId, content);

        String requestBody = om.writeValueAsString(requestDTO);

        ResultActions result = mvc.perform(
                post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("댓글 생성 실패 test1: 존재하지 않는 TIL")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_comment_fail_test_1() throws Exception {

        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 122L;

        String content = "열심히 하셨군요 홍홍";
        CommentRequest.CreateCommentDTO requestDTO = new CommentRequest.CreateCommentDTO(roadmapId, stepId, tilId, content);

        String requestBody = om.writeValueAsString(requestDTO);

        ResultActions result = mvc.perform(
                post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 til을 찾을 수 없습니다"));

    }

    @DisplayName("댓글 생성 실패 test2: 존재하지 않는 step")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_comment_fail_test_2() throws Exception {

        Long roadmapId = 1L;
        Long stepId = 133L;
        Long tilId = 1L;

        String content = "열심히 하셨군요 홍홍";
        CommentRequest.CreateCommentDTO requestDTO = new CommentRequest.CreateCommentDTO(roadmapId, stepId, tilId, content);

        String requestBody = om.writeValueAsString(requestDTO);

        ResultActions result = mvc.perform(
                post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 step을 찾을 수 없습니다."));

    }

    @DisplayName("댓글 생성 실패 test3: 존재하지 않는 로드맵")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void create_comment_fail_test_3() throws Exception {

        Long roadmapId = 13434L;
        Long stepId = 1L;
        Long tilId = 1L;

        String content = "열심히 하셨군요 홍홍";
        CommentRequest.CreateCommentDTO requestDTO = new CommentRequest.CreateCommentDTO(roadmapId, stepId, tilId, content);

        String requestBody = om.writeValueAsString(requestDTO);

        ResultActions result = mvc.perform(
                post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));

    }

    @DisplayName("댓글 수정 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void update_comment_success_test() throws Exception {

        //given
        Long commentId = 2L;
        String content = "수정한 댓글입니다 홍홍";
        CommentRequest.UpdateCommentDTO requestDTO = new CommentRequest.UpdateCommentDTO(content);

        String requestBody = om.writeValueAsString(requestDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/comments/" + commentId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );


        System.out.println("테스트 ---------------------------------- "+content);

        //then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("댓글 수정 실패 test - 권한없음")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void update_comment_fail_test() throws Exception {

        //given
        Long commentId = 3L;

        String content = "수정한 댓글입니다 홍홍";
        CommentRequest.UpdateCommentDTO requestDTO = new CommentRequest.UpdateCommentDTO(content);

        String requestBody = om.writeValueAsString(requestDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/comments/" + commentId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );
        System.out.println("테스트 ---------------------------------- "+content);

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 댓글을 수정할 권한이 없습니다."));
    }

    @DisplayName("댓글 삭제 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void delete_comment_success_test() throws Exception {

        //given

        Long commentId = 2L;

        //when
        ResultActions result = mvc.perform(
                delete("/api/comments/" + commentId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("댓글 삭제 실패 test1: 존재하지 않는 댓글")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void delete_comment_fail_test_1() throws Exception {

        //given
        Long commentId = 23232L;

        //when
        ResultActions result = mvc.perform(
                delete("/api/comments/" + commentId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 댓글을 찾을 수 없습니다."));

    }
    @DisplayName("댓글 삭제 실패 test2: 삭제 권한 없는 경우")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void delete_comment_fail_test_2() throws Exception {

        //given

        Long commentId = 3L;

        //when
        ResultActions result = mvc.perform(
                delete("/api/comments/" + commentId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 댓글을 삭제할 권한이 없습니다."));

    }
}
