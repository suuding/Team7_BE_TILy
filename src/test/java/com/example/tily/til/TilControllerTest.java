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

        Long roadmapId = 1L;
        Long stepId = 1L;

        //when
        ResultActions result = mvc.perform(
                post("/roadmaps/individual/"+ roadmapId +"/steps/"+ stepId +"/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(1));

    }
}
