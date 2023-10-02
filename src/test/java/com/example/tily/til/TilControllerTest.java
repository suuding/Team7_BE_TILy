package com.example.tily.til;

import com.example.tily.step.StepRequest;
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
        result.andExpect(jsonPath("$.message").value("TIL 제목을 입력해주세요."));

    }
}
