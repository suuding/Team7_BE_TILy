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
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void create_til_success_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;

        String title = "spring security";
        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO();
        reqeustDTO.setTitle(title);

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(1));

    }

    @DisplayName("틸 생성 실패 test - 제목 미입력")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void create_til_failed_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;

        String title = "";
        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO();
        reqeustDTO.setTitle(title);

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

    @DisplayName("틸 저장(수정) 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void update_til_test() throws Exception {

        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 1L;

        String content = "바뀐 내용입니다.";
        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO();
        reqeustDTO.setContent(content);

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );
        System.out.println("테스트 ---------------------------------- "+content);

        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("틸 조회 성공 test")
    @WithUserDetails(value = "hong@naver.com")
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
        result.andExpect(jsonPath("$.result.stepTitle").value("스프링 시큐리티"));
        result.andExpect(jsonPath("$.result.content").value("이것은 내용"));
        result.andExpect(jsonPath("$.result.personal").value("true"));

    }

    @DisplayName("틸 조회 실패 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void view_til_failed_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 5L;

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
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void submit_til_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1L;
        Long tilId = 1L;

        String submitContent = "제출할 내용입니다.";
        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO();
        reqeustDTO.setSubmitContent(submitContent);

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- "+submitContent);
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
        Long tilId = 1L;

        //when
        ResultActions result = mvc.perform(
                delete("/roadmaps/"+ roadmapId +"/steps/"+ stepId +"/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        result.andExpect(jsonPath("$.success").value("true"));

    }

}
