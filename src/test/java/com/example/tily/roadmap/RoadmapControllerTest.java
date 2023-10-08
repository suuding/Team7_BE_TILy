package com.example.tily.roadmap;

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
public class RoadmapControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("개인 로드맵_생성_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_individual_create_success_test() throws Exception {

        // given
        String name = "hong";
        RoadmapRequest.CreateIndividualDTO requestDTO = new RoadmapRequest.CreateIndividualDTO();
        requestDTO.setName(name);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/individual")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("개인 로드맵_생성_실패_test:잘못된 이름 형식")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_individual_create_fail_test() throws Exception {

        // given
        String name = "";
        RoadmapRequest.CreateIndividualDTO requestDTO = new RoadmapRequest.CreateIndividualDTO();
        requestDTO.setName(name);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/individual")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }
}
