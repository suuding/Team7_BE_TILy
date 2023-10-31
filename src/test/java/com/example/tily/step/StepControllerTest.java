package com.example.tily.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class StepControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("개인 로드맵 스텝_생성_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void individual_step_create_success_test() throws Exception {

        // given
        Long id = 1L;

        StepRequest.CreateIndividualStepDTO requestDTO = new StepRequest.CreateIndividualStepDTO("스프링 시큐리티 - 세팅");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/individual/"+ id +"/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(11L));
    }

    @DisplayName("개인 로드맵 스텝_생성_실패_test_1: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void individual_step_create_fail_test_1() throws Exception {

        // given
        Long id = 50L;

        StepRequest.CreateIndividualStepDTO requestDTO = new StepRequest.CreateIndividualStepDTO("스프링 시큐리티 - 세팅");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/individual/"+ id +"/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("개인 로드맵 스텝_생성_실패_test_1: 잘못된 제목 형식")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void individual_step_create_fail_test_2() throws Exception {

        // given
        Long id = 1L;

        StepRequest.CreateIndividualStepDTO requestDTO = new StepRequest.CreateIndividualStepDTO("");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/individual/"+ id +"/steps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("레퍼런스_조회_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void find_reference_success_test() throws Exception {
        // given
        Long stepId = 4L;
        Long roadmapsId = 1L;

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/" + roadmapsId + "/steps/"+ stepId +"/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("레퍼런스_조회_실패_test: 존재하지 않은 스텝")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void find_reference_fail_test() throws Exception {
        // given
        Long stepId = 20L;
        Long roadmapsId = 1L;

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/" + roadmapsId + "/steps/"+ stepId +"/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }
}
