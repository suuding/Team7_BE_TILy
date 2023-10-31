package com.example.tily.til;

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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TilControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("틸 생성 성공 test")
    @WithUserDetails(value = "admin@test.com")
    @Test
    public void create_til_success_test() throws Exception {
        //given
        Long roadmapId = 5L;
        Long stepId = 8L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO("spring security");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(15));
    }

    @DisplayName("틸 생성 실패 test - 제목 미입력")
    @WithUserDetails(value = "admin@test.com")
    @Test
    public void create_til_failed_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO("");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        //result.andExpect(jsonPath("$.message").value("TIL 제목을 입력해주세요."));

    }
    @DisplayName("틸 생성 실패 test - 잘못된 roadmapId")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void create_til_failed_test2() throws Exception {
        //given
        Long roadmapId = 15L;
        Long stepId = 1L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO("10월 9일 TIL");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("message").value("해당 roadmap을 찾을 수 없습니다."));

    }

    @DisplayName("틸 저장(수정) 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void update_til_test() throws Exception {

        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 1L;

        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO("바뀐 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );
        System.out.println("테스트 ---------------------------------- " + "바뀐 내용입니다.");

        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("틸 저장(수정) 실패 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void update_til_failed_test() throws Exception {

        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 15L;

        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO("바뀐 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );
        System.out.println("테스트 ---------------------------------- " + "바뀐 내용입니다.");

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 til을 찾을 수 없습니다"));

    }

    @DisplayName("틸 조회 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void view_til_success_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 1L;

        //when
        ResultActions result = mvc.perform(
                get("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.stepId").value(1L));
        result.andExpect(jsonPath("$.result.stepTitle").value("스프링 시큐리티를 사용하는 이유"));
        result.andExpect(jsonPath("$.result.content").value("이것은 내용입니다."));
        result.andExpect(jsonPath("$.result.personal").value("true"));
        result.andExpect(jsonPath("$.result.comments[0].id").value(1L));
        result.andExpect(jsonPath("$.result.comments[0].name").value("su"));


    }

    @DisplayName("틸 조회 실패 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void view_til_failed_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 15L;

        //when
        ResultActions result = mvc.perform(
                get("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("false"));

    }

    @DisplayName("틸 제출 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void submit_til_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 1L;

        LocalDateTime submitDate = LocalDateTime.now();

        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO("제출할 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- " + "제출할 내용입니다.");
        System.out.println("테스트 ---------------------------------- " + submitDate);
        //then
        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("틸 삭제 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void delete_til_test() throws Exception {

        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 2L;

        //when
        ResultActions result = mvc.perform(
                delete("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("나의 틸 전체 목록 조회 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void find_til_success_test() throws Exception {
        // given

        // when
        ResultActions result = mvc.perform(
                get("/tils/my")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.tils[0].id").value(12L));
    }

    @DisplayName("나의 틸 목록 조회 성공 test:제목으로 검색")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void find_til_param_success_test_1() throws Exception {
        // given
        String title = "사용";

        // when
        ResultActions result = mvc.perform(
                get("/tils/my")
                        .param("title", title)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.tils[0].step.title").value("소셜 로그인 사용하기"));
    }

    @DisplayName("나의 틸 목록 조회 성공 test:로드맵id로 검색")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void find_til_param_success_test_2() throws Exception {
        // given
        Long roadmapId = 1L;

        // when
        ResultActions result = mvc.perform(
                get("/tils/my")
                        .param("roadmapId", roadmapId.toString())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.tils[0].roadmap.id").value(1L));
    }

    @DisplayName("나의 틸 목록 조회 실패 test:잘못된 날짜 형식")
    @WithUserDetails("tngus@test.com")
    @Test
    public void find_til_param_fail_test() throws Exception {
        // given
        String date = "12345";

        // when
        ResultActions result = mvc.perform(
                get("/tils/my")
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("입력한 날짜를 찾을 수 없습니다."));
    }
}
