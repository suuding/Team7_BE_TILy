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

import java.util.ArrayList;
import java.util.List;

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
        RoadmapRequest.CreateIndividualRoadmapDTO requestDTO = new RoadmapRequest.CreateIndividualRoadmapDTO();
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
        RoadmapRequest.CreateIndividualRoadmapDTO requestDTO = new RoadmapRequest.CreateIndividualRoadmapDTO();
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

    @DisplayName("그룹 로드맵_생성_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_create_success_test() throws Exception {

        // given
        RoadmapRequest.CreateGroupRoadmapDTO requestDTO = new RoadmapRequest.CreateGroupRoadmapDTO();

        // 로드맵
        RoadmapRequest.CreateGroupRoadmapDTO.RoadmapDTO roadmap = new RoadmapRequest.CreateGroupRoadmapDTO.RoadmapDTO();
        roadmap.setName("운영체제(OS) 스터디");
        roadmap.setDescription("면접 대비를 위한 CS 스터디 모임입니다!");
        roadmap.setIsPublic(true);

        requestDTO.setRoadmap(roadmap);

        // 스텝
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO> steps = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO step1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO();
        step1.setTitle("데드락(Deadlock)");
        step1.setDescription("스텝 1");

        // step1의 참조
        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs references1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs();

        // step1의 참조 - youtube
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeReference1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        youtubeReference1.setLink("https://www.youtube.com/watch?v=v3slRhISacM");
        youtubeReferences.add(youtubeReference1);
        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeReference2 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        youtubeReference2.setLink("https://www.youtube.com/watch?v=OGXWr8LdKW0");
        youtubeReferences.add(youtubeReference2);

        references1.setYoutube(youtubeReferences); // references1의 youtube 설정

        // step1의 참조 - web
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webReference1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        webReference1.setLink("https://blog.naver.com/hoyai-/223178676752");
        webReferences.add(webReference1);
        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webReference2 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        webReference2.setLink("https://blog.naver.com/hoyai-/223173899248");
        webReferences.add(webReference2);

        references1.setWeb(webReferences); // references1의 web 설정

        step1.setReferences(references1); // step1의 references 설정

        steps.add(step1);
        requestDTO.setSteps(steps); // requestDTO의 steps 설정

        //
        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(6));
    }

    @DisplayName("그룹 로드맵_생성_실패_test: ")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_create_fail_test() throws Exception {

        // given
        RoadmapRequest.CreateGroupRoadmapDTO requestDTO = new RoadmapRequest.CreateGroupRoadmapDTO();

        // 로드맵
        RoadmapRequest.CreateGroupRoadmapDTO.RoadmapDTO roadmap = new RoadmapRequest.CreateGroupRoadmapDTO.RoadmapDTO();
        roadmap.setName("");
        roadmap.setDescription("면접 대비를 위한 CS 스터디 모임입니다!");
        roadmap.setIsPublic(true);

        requestDTO.setRoadmap(roadmap);

        // 스텝
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO> steps = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO step1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO();
        step1.setTitle("데드락(Deadlock)");
        step1.setDescription("스텝 1");

        // step1의 참조
        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs references1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs();

        // step1의 참조 - youtube
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeReference1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        youtubeReference1.setLink("https://www.youtube.com/watch?v=v3slRhISacM");
        youtubeReferences.add(youtubeReference1);
        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeReference2 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        youtubeReference2.setLink("https://www.youtube.com/watch?v=OGXWr8LdKW0");
        youtubeReferences.add(youtubeReference2);

        references1.setYoutube(youtubeReferences); // references1의 youtube 설정

        // step1의 참조 - web
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webReference1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        webReference1.setLink("https://blog.naver.com/hoyai-/223178676752");
        webReferences.add(webReference1);
        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webReference2 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        webReference2.setLink("https://blog.naver.com/hoyai-/223173899248");
        webReferences.add(webReference2);

        references1.setWeb(webReferences); // references1의 web 설정

        step1.setReferences(references1); // step1의 references 설정

        steps.add(step1);
        requestDTO.setSteps(steps); // requestDTO의 steps 설정

        //
        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.id").value(6));
    }



    //String responseBody = result.andReturn().getResponse().getContentAsString();
    //System.out.println("테스트 : "+responseBody);
}
