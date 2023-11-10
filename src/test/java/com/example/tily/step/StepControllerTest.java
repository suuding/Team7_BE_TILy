package com.example.tily.step;

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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class StepControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("스텝_생성_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void step_create_success_test() throws Exception {

        // given
        Long roadmapId = 1L;
        String title = "스프링 시큐리티 - 세팅";
        String description = "7조 화이팅";
        LocalDateTime dueDate = LocalDateTime.of(2023, 11, 12, 12, 32,22,3333);

        StepRequest.CreateStepDTO requestDTO = new StepRequest.CreateStepDTO(title,roadmapId, description, dueDate);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(12L));
    }

    @DisplayName("스텝_생성_실패_test_1: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void step_create_fail_test_1() throws Exception {

        // given
        Long roadmapId = 50L;
        String title = "스프링 시큐리티 - 세팅";
        String description = "7조 화이팅";
        LocalDateTime dueDate = LocalDateTime.of(
                2023, 11, 12, 12, 32,22,3333);

        StepRequest.CreateStepDTO requestDTO = new StepRequest.CreateStepDTO(title, roadmapId, description, dueDate);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("스텝_생성_실패_test_2: 잘못된 제목 형식")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void step_create_fail_test_2() throws Exception {

        // given
        Long roadmapId = 1L;
        String title = "";
        String description = "7조 화이팅";
        LocalDateTime dueDate = LocalDateTime.of(
                2023, 11, 12, 12, 32,22,3333);

        StepRequest.CreateStepDTO requestDTO = new StepRequest.CreateStepDTO(title, roadmapId, description, dueDate);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("step의 제목을 입력해주세요."));
    }

    @DisplayName("스텝_조회_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void step_view_success_test() throws Exception {

        // given
        Long roadmapId = 1L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/" + roadmapId + "/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("스텝_조회_실패_test_1: 존재하지 않는 로드맵")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void step_view_fail_test_1() throws Exception {

        // given
        Long roadmapId = 1044L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/" + roadmapId + "/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("스텝_수정_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void step_update_success_test() throws Exception {

        // given
        Long stepId = 1L;
        String title = "알고리즘 - 수업 소개";
        String description = "7조 화이팅!";
        LocalDateTime dueDate = LocalDateTime.of(
                2023, 11, 12, 12, 32,22,3333);

        StepRequest.UpdateStepDTO requestDTO = new StepRequest.UpdateStepDTO(title,description, dueDate);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/steps/" + stepId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("스텝_수정_실패_test_1: 로드맵에 속하지 않은 유저의 접근")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void step_update_fail_test_1() throws Exception {

        // given
        Long stepId = 10L;
        String title = "스프링시큐리티";
        String description = "7조 화이팅!";
        LocalDateTime dueDate = LocalDateTime.of(
                2023, 11, 12, 12, 32,22,3333);

        StepRequest.UpdateStepDTO requestDTO = new StepRequest.UpdateStepDTO(title,description, dueDate);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/steps/" + stepId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );



        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 속하지 않습니다."));
    }

    @DisplayName("스텝_수정_실패_test_2: 존재하지 않는 스텝")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void step_update_fail_test_2() throws Exception {

        // given
        Long stepId = 1044L;
        String title = "스프링시큐리티";
        String description = "7조 화이팅!";
        LocalDateTime dueDate = LocalDateTime.of(
                2023, 11, 12, 12, 32,22,3333);

        StepRequest.UpdateStepDTO requestDTO = new StepRequest.UpdateStepDTO(title,description, dueDate);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/steps/" + stepId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );



        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 step을 찾을 수 없습니다."));
    }



    @DisplayName("스텝_삭제_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void step_delete_success_test() throws Exception {

        // given
        Long stepId = 1L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/steps/" + stepId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );



        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }


}
