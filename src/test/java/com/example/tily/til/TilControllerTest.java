package com.example.tily.til;

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
public class TilControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("틸 생성 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_til_success_test() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 11L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO(roadmapId,stepId,"스프링 시큐리티 세팅");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/api/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("틸 생성 실패 test1 - 제목 미입력")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_til_fail_test_1() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 4L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO(roadmapId, stepId, "");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/api/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("TIL 제목을 입력해주세요."));

    }

    @DisplayName("틸 생성 실패 test2 - 이미 til이 존재하는 step")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_til_fail_test_2() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 3L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO(roadmapId, stepId, "알고리즘");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/api/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 step에 대한 til이 이미 존재합니다."));

    }


    @DisplayName("틸 생성 실패 test3: 잘못된 roadmapId")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_til_fail_test_3() throws Exception {
        //given
        Long roadmapId = 105L;
        Long stepId = 1L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO(roadmapId, stepId, "스프링 시큐리티 세팅");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/api/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("message").value("해당 roadmap을 찾을 수 없습니다."));

    }

    @DisplayName("틸 생성 실패 test4: 잘못된 stepId")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void create_til_fail_test_4() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 1232L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO(roadmapId, stepId, "스프링 시큐리티 세팅");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/api/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("message").value("해당 step을 찾을 수 없습니다."));

    }

    @DisplayName("틸 생성 실패 test5: 권한 없는 유저")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void create_til_fail_test_5() throws Exception {
        //given
        Long roadmapId = 1L;
        Long stepId = 4L;

        TilRequest.CreateTilDTO reqeustDTO = new TilRequest.CreateTilDTO(roadmapId, stepId, "스프링 시큐리티 세팅");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                post("/api/tils")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("message").value("해당 로드맵에 til을 생성할 권한이 없습니다."));

    }

    @DisplayName("틸 저장(수정) 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void update_til_success_test() throws Exception {

        //given
        Long tilId = 1L;

        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO("바뀐 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );
        System.out.println("테스트 ---------------------------------- " + "바뀐 내용입니다.");

        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("틸 저장(수정) 실패 test1: 존재하지 않는 til")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void update_til_fail_test_1() throws Exception {

        //given
        Long tilId = 15L;

        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO("바뀐 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );
        System.out.println("테스트 ---------------------------------- " + "바뀐 내용입니다.");

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 til을 찾을 수 없습니다"));

    }

    @DisplayName("틸 저장(수정) 실패 test2: 내용 미입력")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void update_til_fail_test_2() throws Exception {

        //given
        Long tilId = 1L;

        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO("");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("TIL 내용을 입력해주세요."));

    }

    @DisplayName("틸 저장(수정) 실패 test3: 권한 없는 경우")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void update_til_fail_test_3() throws Exception {

        //given
        Long tilId = 5L;

        TilRequest.UpdateTilDTO reqeustDTO = new TilRequest.UpdateTilDTO("바뀐 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("til에 대한 권한이 없습니다."));

    }

    @DisplayName("틸 조회 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void view_til_success_test() throws Exception {
        //given
        Long tilId = 5L;

        //when
        ResultActions result = mvc.perform(
                get("/api/tils/" + tilId)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.content").value("이것은 내용입니다1."));
        result.andExpect(jsonPath("$.result.submitContent").value("이것은 제출할 내용입니다."));
        result.andExpect(jsonPath("$.result.isPersonal").value("false"));
        result.andExpect(jsonPath("$.result.isSubmit").value("true"));
        result.andExpect(jsonPath("$.result.step.id").value("5"));

    }

    @DisplayName("틸 조회 실패 test1: 존재하지 않는 til")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void view_til_fail_test_1() throws Exception {
        //given
        Long tilId = 151L;

        //when
        ResultActions result = mvc.perform(
                get("/api/tils/" + tilId)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 til을 찾을 수 없습니다"));

    }

    @DisplayName("틸 조회 실패 test2: 권한 없는 유저")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void view_til_failed_test_2() throws Exception {
        //given
        Long tilId = 4L;

        //when
        ResultActions result = mvc.perform(
                get("/api/tils/" + tilId)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("권한이 없습니다."));

    }

    @DisplayName("틸 제출 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void submit_til_success_test() throws Exception {
        //given
        Long tilId = 7L;

        LocalDateTime submitDate = LocalDateTime.now();

        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO("제출할 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/api/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- " + requestBody);
        System.out.println("테스트 ---------------------------------- " + submitDate);
        //then
        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("틸 제출 실패 test1: 접근 권한 없는 경우")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void submit_til_fail_test_1() throws Exception {
        //given
        Long tilId = 1L;

        LocalDateTime submitDate = LocalDateTime.now();

        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO("제출할 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/api/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- " + requestBody);
        System.out.println("테스트 ---------------------------------- " + submitDate);
        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("til에 대한 권한이 없습니다."));

    }

    @DisplayName("틸 제출 실패 test2: til을 제출할 권한이 없는 경우")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void submit_til_fail_test_2() throws Exception {
        //given
        Long tilId = 1L;

        LocalDateTime submitDate = LocalDateTime.now();

        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO("제출할 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/api/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- " + requestBody);
        System.out.println("테스트 ---------------------------------- " + submitDate);
        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("til을 제출할 권한이 없습니다."));

    }

    @DisplayName("틸 제출 실패 test3: 본인의 til이 아닌 경우")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void submit_til_fail_test_3() throws Exception {
        //given
        Long tilId = 1L;

        LocalDateTime submitDate = LocalDateTime.now();

        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO("제출할 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/api/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- " + requestBody);
        System.out.println("테스트 ---------------------------------- " + submitDate);
        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("til에 대한 권한이 없습니다."));

    }

    @DisplayName("틸 제출 실패 test4: 이미 제출한 경우")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void submit_til_fail_test_4() throws Exception {
        //given
        Long tilId = 5L;

        LocalDateTime submitDate = LocalDateTime.now();

        TilRequest.SubmitTilDTO reqeustDTO = new TilRequest.SubmitTilDTO("제출할 내용입니다.");

        String requestBody = om.writeValueAsString(reqeustDTO);
        //when
        ResultActions result = mvc.perform(
                post("/api/tils/" + tilId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        System.out.println("테스트 ---------------------------------- " + requestBody);
        System.out.println("테스트 ---------------------------------- " + submitDate);
        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("이미 til을 제출하였습니다."));

    }
    @DisplayName("틸 삭제 성공 test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void delete_til_success_test() throws Exception {

        //given
        Long tilId = 6L;

        //when
        ResultActions result = mvc.perform(
                delete("/api/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("true"));

    }

    @DisplayName("틸 삭제 실패 test1: 본인의 til이 아닌 경우")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void delete_til_fail_test_1() throws Exception {

        //given
        Long tilId = 7L;

        //when
        ResultActions result = mvc.perform(
                delete("/api/tils/" + tilId )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("til에 대한 권한이 없습니다."));

    }

    @DisplayName("나의 틸 전체 목록 조회 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void find_til_success_test() throws Exception {
        // given

        // when
        ResultActions result = mvc.perform(
                get("/api/tils/my")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("나의 틸 목록 조회 성공 test: 제목으로 검색")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void find_til_param_success_test_1() throws Exception {
        // given
        String title = "사용";

        // when
        ResultActions result = mvc.perform(
                get("/api/tils/my")
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
                get("/api/tils/my")
                        .param("roadmapId", roadmapId.toString())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("나의 틸 목록 조회 실패 test: 잘못된 날짜 형식")
    @WithUserDetails("tngus@test.com")
    @Test
    public void find_til_param_fail_test() throws Exception {
        // given
        String date = "12345";

        // when
        ResultActions result = mvc.perform(
                get("/api/tils/my")
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("잘못된 날짜 형식입니다."));
    }
}
