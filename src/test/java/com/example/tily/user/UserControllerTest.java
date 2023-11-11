package com.example.tily.user;

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
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("이메일_중복확인_성공_test")
    @Test
    public void user_email_check_success_test() throws Exception {

        // given
        UserRequest.CheckEmailDTO requestDTO = new UserRequest.CheckEmailDTO("tngus1@test.com");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/email/check")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        String response = result.andReturn().getResponse().getContentAsString();
        System.out.println("response = " + response);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("사용자_인증코드_전송_성공_test")
    @Test
    public void user_send_email_code_success_test() throws Exception {

        // given
        UserRequest.SendEmailCodeDTO requestDTO = new UserRequest.SendEmailCodeDTO("tngus@test.com");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/email/code")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("사용자_인증코드_전송_실패_test1:존재하지 않는 이메일")
    @Test
    public void user_send_email_code_fail_test_1() throws Exception {

        // given
        UserRequest.SendEmailCodeDTO requestDTO = new UserRequest.SendEmailCodeDTO("test1@test.com");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/email/code")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 이메일을 찾을 수 없습니다."));
    }

    @DisplayName("사용자_회원가입_성공_test")
    @Test
    public void user_join_success_test() throws Exception {

        // given
        UserRequest.JoinDTO requestDTO = new UserRequest.JoinDTO("test@nate.com", "test", "test1234!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.message").value("Created"));
    }

    @DisplayName("사용자_회원가입_실패_test_1:잘못된 이메일 형식")
    @Test
    public void user_join_fail_test_1() throws Exception {

        // given
        UserRequest.JoinDTO requestDTO = new UserRequest.JoinDTO("testnate.com", "test", "test1234!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("올바른 이메일 형식을 입력해주세요."));
    }

    @DisplayName("사용자_회원가입_실패_test_2:잘못된 비밀번호 형식")
    @Test
    public void user_join_fail_test_2() throws Exception {

        // given
        UserRequest.JoinDTO requestDTO = new UserRequest.JoinDTO("test@nate.com", "test", "test1234");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("올바른 비밀번호 형식을 입력해주세요."));
    }

    @DisplayName("사용자_회원가입_실패_test_3:잘못된 비밀번호 길이")
    @Test
    public void user_join_fail_test_3() throws Exception {

        // given
        UserRequest.JoinDTO requestDTO = new UserRequest.JoinDTO("test@nate.com", "test", "te");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("사용자_로그인_성공_test")
    @Test
    public void user_login_success_test() throws Exception {

        // given
        UserRequest.LoginDTO requestDTO = new UserRequest.LoginDTO("tngus@test.com", "hongHong1!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("사용자_로그인_실패_1:존재하지 않는 이메일")
    @Test
    public void user_login_fail_test_1() throws Exception {

        // given
        UserRequest.LoginDTO requestDTO = new UserRequest.LoginDTO("tngus1@test.com", "test1234!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 이메일을 찾을 수 없습니다."));
    }

    @DisplayName("사용자_로그인_실패_2:비밀번호 불일치")
    @Test
    public void user_login_fail_test_2() throws Exception {

        // given
        UserRequest.LoginDTO requestDTO = new UserRequest.LoginDTO("tngus@test.com", "Honghong123!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @DisplayName("사용자_비밀번호_재설정_성공_test")
    @Test
    public void user_change_password_success_test() throws Exception {

        // given
        UserRequest.ChangePwdDTO requestDTO = new UserRequest.ChangePwdDTO("tngus@test.com", "meta1234!!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/password/change")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("사용자_비밀번호_재설정_실패_test_1:존재하지 않는 이메일")
    @Test
    public void user_change_password_fail_test_1() throws Exception {

        // given
        UserRequest.ChangePwdDTO requestDTO = new UserRequest.ChangePwdDTO("tngus1@pusan.ac.kr", "meta1234!!");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/password/change")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 이메일을 찾을 수 없습니다."));
    }

    @DisplayName("사용자_비밀번호_재설정_실패_test_2:잘못된 비밀번호 형식")
    @Test
    public void user_change_password_fail_test_2() throws Exception {

        // given
        UserRequest.ChangePwdDTO requestDTO = new UserRequest.ChangePwdDTO("tngus@pusan.ac.kr", "meta1234");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/password/change")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("올바른 비밀번호 형식을 입력해주세요."));
    }

//    @DisplayName("장미밭 조회 성공 test")
//    @WithUserDetails(value = "tngus@test.com")
//    @Test
//    public void view_gardens_test() throws Exception {
//
//
//        ResultActions result = mvc.perform(
//                get("/api/gardens")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//        );
//
//        String responseBody = result.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        result.andExpect(jsonPath("$.success").value("true"));
//    }


    @DisplayName("사용자_탈퇴_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void withdrawMembership_test() throws Exception {

        // given

        // when
        ResultActions result = mvc.perform(
                delete("/api/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

}

