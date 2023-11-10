package com.example.tily.step.reference;


import com.example.tily.step.StepRequest;
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
public class ReferenceControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("참고자료_생성_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_create_success_test() throws Exception {

        // given
        Long stepId = 1L;
        String link = "https://www.naver.com";
        String category = "web";

        ReferenceRequest.CreateReferenceDTO requestDTO = new ReferenceRequest.CreateReferenceDTO(stepId, category, link);


        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("참고자료_생성_실패_test_1: 존재하지 않는 step")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_create_fail_test_1() throws Exception {

        // given
        Long stepId = 1355L;
        String link = "https://www.naver.com";
        String category = "web";

        ReferenceRequest.CreateReferenceDTO requestDTO = new ReferenceRequest.CreateReferenceDTO(stepId, category, link);


        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 step을 찾을 수 없습니다."));

    }

    @DisplayName("참고자료_생성_실패_test_2: 카테고리 미입력")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_create_fail_test_2() throws Exception {

        // given
        Long stepId = 1L;
        String link = "https://www.naver.com";
        String category = "";

        ReferenceRequest.CreateReferenceDTO requestDTO = new ReferenceRequest.CreateReferenceDTO(stepId, category, link);


        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("참고자료의 카테고리를 입력해주세요."));

    }

    @DisplayName("참고자료_생성_실패_test_3: 참고자료 링크 미입력")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_create_fail_test_3() throws Exception {

        // given
        Long stepId = 1L;
        String link = "";
        String category = "youtube";

        ReferenceRequest.CreateReferenceDTO requestDTO = new ReferenceRequest.CreateReferenceDTO(stepId, category, link);


        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("링크를 입력해주세요."));

    }

    @DisplayName("참고자료_조회_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_view_success_test() throws Exception {

        // given
        Long stepId = 1L;

        // when
        ResultActions result = mvc.perform(
                get("/api/steps/" + stepId + "/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("참고자료_조회_실패_test: 존재하지 않는 스텝")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_view_fail_test() throws Exception {

        // given
        Long stepId = 13434L;

        // when
        ResultActions result = mvc.perform(
                get("/api/steps/" + stepId + "/references")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));

    }

    @DisplayName("참고자료_삭제_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_delete_success_test() throws Exception {

        // given
        Long referenceId = 1L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/references/" + referenceId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("참고자료_삭제_실패_test_1: 로드맵에 속하지 않은 유저의 시도")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void reference_delete_fail_test_1() throws Exception {

        // given
        Long referenceId = 1L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/references/" + referenceId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 속하지 않습니다."));

    }

    @DisplayName("참고자료_삭제_실패_test_2: 존재하지 않는 레퍼런스")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void reference_delete_fail_test_2() throws Exception {

        // given
        Long referenceId = 1333L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/references/" + referenceId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 reference를 찾을 수 없습니다."));

    }

}
