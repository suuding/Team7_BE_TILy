package com.example.tily.Alarm;

import com.example.tily.alarm.AlarmRequest;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AlarmControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("알림_조회_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void alarm_find_success_test() throws Exception {

        // given

        // when
        ResultActions result = mvc.perform( 
                get("/api/alarms")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("알림 읽음 처리 성공 test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void alarm_read_success_test() throws Exception {

        //given
        List<AlarmRequest.ReadAlarmDTO.AlarmDTO> alarms = new ArrayList<>();
        AlarmRequest.ReadAlarmDTO requestDTO = new AlarmRequest.ReadAlarmDTO(alarms);
        String requestBody = om.writeValueAsString(requestDTO);

        //when
        ResultActions result = mvc.perform(
                patch("/api/alarms/read")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        //then
        result.andExpect(jsonPath("$.success").value("true"));
    }
}
