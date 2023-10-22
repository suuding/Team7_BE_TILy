package com.example.tily.roadmap;

import com.example.tily.roadmap.relation.GroupRole;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        step1.setDueDate(LocalDateTime.of(2023, 10, 1, 19 ,28));

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
    }

    // 실패 케이스는 화면을 바탕으로 만듦
    @DisplayName("그룹 로드맵_생성_실패_test: 로드맵 이름을 입력하지 않음")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_create_fail_test() throws Exception {
        // given
        RoadmapRequest.CreateGroupRoadmapDTO requestDTO = new RoadmapRequest.CreateGroupRoadmapDTO();

        // 로드맵
        RoadmapRequest.CreateGroupRoadmapDTO.RoadmapDTO roadmap = new RoadmapRequest.CreateGroupRoadmapDTO.RoadmapDTO();
        roadmap.setName(null);
        roadmap.setDescription("면접 대비를 위한 CS 스터디 모임입니다!");
        roadmap.setIsPublic(true);

        requestDTO.setRoadmap(roadmap);

        // 스텝
        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO> steps = new ArrayList<>();

        RoadmapRequest.CreateGroupRoadmapDTO.StepDTO step1 = new RoadmapRequest.CreateGroupRoadmapDTO.StepDTO();
        step1.setTitle("데드락(Deadlock)");
        step1.setDescription("스텝 1");
        step1.setDueDate(LocalDateTime.of(2023, 10, 1, 19 ,28));

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

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("그룹 로드맵_조회_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_find_success_test() throws Exception {
        // given
        Long id = 12L;

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.creator.name").value("hong"));
        result.andExpect(jsonPath("$.result.name").value("JPA 스터디"));
        result.andExpect(jsonPath("$.result.code").value("hoyai123"));
        result.andExpect(jsonPath("$.result.steps[0].title").value("다형성(Polymorphism)"));
        result.andExpect(jsonPath("$.result.steps[0].references.web[0].id").value(5L));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("그룹 로드맵_조회_실패_test: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_find_fail_test() throws Exception {
        // given
        Long id = 20L;

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("그룹 로드맵_수정_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_update_success_test() throws Exception {
        // given
        Long id = 4L;
        RoadmapRequest.UpdateGroupRoadmapDTO requestDTO = new RoadmapRequest.UpdateGroupRoadmapDTO();

        // 로드맵
        RoadmapRequest.UpdateGroupRoadmapDTO.RoadmapDTO roadmap = new RoadmapRequest.UpdateGroupRoadmapDTO.RoadmapDTO();

        roadmap.setName("new JAVA - 생활 코딩");
        roadmap.setDescription("새로운 버젼 입니다");
        roadmap.setCode("modifiedCode1234");
        roadmap.setIsRecruit(false);
        roadmap.setIsPublic(true);

        requestDTO.setRoadmap(roadmap);

        // 스텝
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO> steps = new ArrayList<>();

        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO step1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO();
        step1.setId(4L);
        step1.setTitle("다형성(Polymorphism)");
        step1.setDescription("수정된 step description 입니다");

        // step1의 참조
        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs references1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs();

        // step1의 참조 - youtube
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeReference1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        youtubeReference1.setId(1L);
        youtubeReference1.setLink("https://www.youtube.com/watch?v=수정된 주소");
        youtubeReferences.add(youtubeReference1);

        references1.setYoutube(youtubeReferences); // references1의 youtube 설정

        // step1의 참조 - web
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webReference1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        webReference1.setId(4L);
        webReference1.setLink("https://blog.naver.com/hoyai-/수정된 주소");
        webReferences.add(webReference1);

        references1.setWeb(webReferences); // references1의 web 설정

        step1.setReferences(references1); // step1의 references 설정

        steps.add(step1);
        requestDTO.setSteps(steps); // requestDTO의 steps 설정

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    // 실패 케이스는 화면을 바탕으로 만듦
    @DisplayName("그룹 로드맵_수정_실패_test: 로드맵 이름을 입력하지 않음")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_update_fail_test() throws Exception {
        // given
        Long id = 4L;
        RoadmapRequest.UpdateGroupRoadmapDTO requestDTO = new RoadmapRequest.UpdateGroupRoadmapDTO();

        // 로드맵
        RoadmapRequest.UpdateGroupRoadmapDTO.RoadmapDTO roadmap = new RoadmapRequest.UpdateGroupRoadmapDTO.RoadmapDTO();

        roadmap.setName(null);
        roadmap.setDescription("새로운 버젼 입니다");
        roadmap.setCode("modifiedCode1234");
        roadmap.setIsRecruit(false);
        roadmap.setIsPublic(true);

        requestDTO.setRoadmap(roadmap);

        // 스텝
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO> steps = new ArrayList<>();

        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO step1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO();
        step1.setId(4L);
        step1.setTitle("다형성(Polymorphism)");
        step1.setDescription("수정된 step description 입니다");

        // step1의 참조
        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs references1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs();

        // step1의 참조 - youtube
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeReference1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        youtubeReference1.setId(1L);
        youtubeReference1.setLink("https://www.youtube.com/watch?v=수정된 주소");
        youtubeReferences.add(youtubeReference1);

        references1.setYoutube(youtubeReferences); // references1의 youtube 설정

        // step1의 참조 - web
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webReference1 = new RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO();
        webReference1.setId(4L);
        webReference1.setLink("https://blog.naver.com/hoyai-/수정된 주소");
        webReferences.add(webReference1);

        references1.setWeb(webReferences); // references1의 web 설정

        step1.setReferences(references1); // step1의 references 설정

        steps.add(step1);
        requestDTO.setSteps(steps); // requestDTO의 steps 설정

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("내가 속한 로드맵 전체 목록_조회_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void roadmap_my_find_success_test () throws Exception {

        // given

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/my")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.categories[0].id").value(1L));
        result.andExpect(jsonPath("$.result.roadmaps.tilys[0].id").value(4L));
    }

    @DisplayName("로드맵_조회_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void roadmap_find_success_test() throws Exception {

        // given

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.category").value("tily"));
        result.andExpect(jsonPath("$.result.roadmaps[0].id").value(9L));
    }

    @DisplayName("로드맵_조회_파라미터_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void roadmap_find_paging_success_test() throws Exception {

        // given
        String category = "group";
        String name = "JAVA 입문 수업 - 생활 코딩";

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps")
                        .param("category", category)
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.category").value("group"));
        result.andExpect(jsonPath("$.result.roadmaps[0].name").value("JAVA 입문 수업 - 생활 코딩"));
    }

    @DisplayName("로드맵_신청하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_apply_success_test() throws Exception{
        // given
        Long id = 10L;

        RoadmapRequest.ApplyRoadmapDTO requestDTO = new RoadmapRequest.ApplyRoadmapDTO();
        String content = "안녕하세요, 반갑습니다~";
        requestDTO.setContent(content);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/" + id + "/apply")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)

        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("로드맵_신청하기_실패_test: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_apply_fail_test() throws Exception{
        // given
        Long id = 20L;

        RoadmapRequest.ApplyRoadmapDTO requestDTO = new RoadmapRequest.ApplyRoadmapDTO();
        String content = "안녕하세요, 반갑습니다~";
        requestDTO.setContent(content);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/" + id + "/apply")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("로드맵_참여하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_participate_success_test() throws Exception{
        // given
        String code = "pnu12345";
        RoadmapRequest.ParticipateRoadmapDTO requestDTO = new RoadmapRequest.ParticipateRoadmapDTO();
        requestDTO.setCode(code);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/groups/participate")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("로드맵_참여하기_실패_test: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_participate_fail_test() throws Exception{
        // given
        String code = "pnu12347";
        RoadmapRequest.ParticipateRoadmapDTO requestDTO = new RoadmapRequest.ParticipateRoadmapDTO();
        requestDTO.setCode(code);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/groups/participate")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("로드맵_구성원_전체조회_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_members_find_success_test() throws Exception{
        // given
        Long id = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+id+"/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.users[0].name").value("hong"));
        result.andExpect(jsonPath("$.result.users[0].role").value("ROLE_MANAGER"));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("로드맵_구성원_전체조회_실패_test: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_members_find_fail_test() throws Exception{
        // given
        Long id = 20L;

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ id +"/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("구성원_역할_변경하기_성공_test")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_role_change_success_test() throws Exception{
        // given
        Long groupsId = 12L;
        Long usersId = 2L;
        GroupRole role = GroupRole.ROLE_MANAGER;
        RoadmapRequest.ChangeMemberRoleDTO requestDTO = new RoadmapRequest.ChangeMemberRoleDTO();
        requestDTO.setRole(role);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/roadmaps/groups/"+ groupsId +"/members/"+ usersId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("구성원_역할_변경하기_실패_test1: 존재하지 않은 유저")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_role_change_fail_test_1() throws Exception{
        // given
        Long groupsId = 12L;
        Long usersId = 10L;
        GroupRole role = GroupRole.ROLE_MANAGER;
        RoadmapRequest.ChangeMemberRoleDTO requestDTO = new RoadmapRequest.ChangeMemberRoleDTO();
        requestDTO.setRole(role);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/roadmaps/groups/"+ groupsId +"/members/"+ usersId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("구성원_역할_변경하기_실패_test2: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_role_change_fail_test_2() throws Exception{
        // given
        Long groupsId = 20L;
        Long usersId = 2L;
        GroupRole role = GroupRole.ROLE_MANAGER;
        RoadmapRequest.ChangeMemberRoleDTO requestDTO = new RoadmapRequest.ChangeMemberRoleDTO();
        requestDTO.setRole(role);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/roadmaps/groups/"+ groupsId +"/members/"+ usersId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("구성원_강퇴하기_성공_test")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_dismiss_success_test() throws Exception {
        // given
        Long groupsId = 12L;
        Long usersId = 1L;

        // when
        ResultActions result = mvc.perform(
                delete("/roadmaps/groups/" + groupsId + "/members/" + usersId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("구성원_강퇴하기_실패_test1: 존재하지 않는 유저")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_dismiss_fail_test_1() throws Exception {
        // given
        Long groupsId = 12L;
        Long usersId = 10L;

        // when
        ResultActions result = mvc.perform(
                delete("/roadmaps/groups/" + groupsId + "/members/" + usersId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("구성원_강퇴하기_실패_test2: 존재하지 않는 로드맵")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_dismiss_fail_test_2() throws Exception {
        // given
        Long groupsId = 20L;
        Long usersId = 1L;

        // when
        ResultActions result = mvc.perform(
                delete("/roadmaps/groups/" + groupsId + "/members/" + usersId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("신청자_조회하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void applied_user_find_success_test() throws Exception {
        // given
        Long groupsId = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ groupsId +"/members/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.users[0].name").value("applier"));
        result.andExpect(jsonPath("$.result.users[0].content").value("참가 신청합니다"));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("신청자_조회하기_실패_test: 존재하지 않는 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void applied_user_find_fail_test() throws Exception {
        // given
        Long groupsId = 20L;

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ groupsId +"/members/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("신청_승인하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_accept_success_test() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 6L;

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("신청_승인하기_실패1_test: 존재하지 않은 유저")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_accept_fail_test_1() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("신청_승인하기_실패2_test: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_accept_fail_test_2() throws Exception {
        // given
        Long groupsId = 20L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                post("/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("신청_거절하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_reject_success_test() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 6L;

        // when
        ResultActions result = mvc.perform(
                delete("/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("신청_거절하기_실패1_test: 존재하지 않은 유저")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_reject_fail_test_1() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                delete("/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("신청_거절하기_실패1_test2: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_reject_fail_test_2() throws Exception {
        // given
        Long groupsId = 20L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                delete("/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("특정스텝_틸_조회하기_성공_test1: isSubmit이 true인 케이스")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void til_find_success_test_1() throws Exception {
        // given
        Long groupsId = 12L;
        Long stepsId = 5L;

        String isSubmit = "true";
        String isMember = "true";

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ groupsId +"/steps/"+ stepsId +"/tils")
                        .param("isSubmit", isSubmit)
                        .param("isMember", isMember)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.members[0].name").value("su"));
        result.andExpect(jsonPath("$.result.members[0].content").value("이것은 내용입니다1."));
        result.andExpect(jsonPath("$.result.members[1].submitDate").value("2023-10-10"));
        result.andExpect(jsonPath("$.result.members[1].commentNum").value("2"));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("특정스텝_틸_조회하기_성공2_test: isSubmit이 false인 케이스")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void til_find_success_test_2() throws Exception {
        // given
        Long groupsId = 12L;
        Long stepsId = 6L;

        String isSubmit = "false";
        String isMember = "true";

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ groupsId +"/steps/"+ stepsId +"/tils")
                        .param("isSubmit", isSubmit)
                        .param("isMember", isMember)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.members[0].name").value("su"));
    }

    @DisplayName("특정스텝_틸_조회하기_성공3_test: name 쿼리 사용")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void til_find_success_test_3() throws Exception {
        // given
        Long groupsId = 12L;
        Long stepsId = 6L;
        String name = "hong";

        String isSubmit = "true";
        String isMember = "true";

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ groupsId +"/steps/"+ stepsId +"/tils")
                        .param("isSubmit", isSubmit)
                        .param("isMember", isMember)
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.members[0].name").value("hong"));
    }

    @DisplayName("특정스텝_틸_조회하기_성공4_test: isMember가 false, 즉 매니저를 포함해서 모든 사람의 til 반환")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void til_find_success_test_4() throws Exception {
        // given
        Long groupsId = 12L;
        Long stepsId = 6L;

        String isSubmit = "true";
        String isMember = "false";

        // when
        ResultActions result = mvc.perform(
                get("/roadmaps/groups/"+ groupsId +"/steps/"+ stepsId +"/tils")
                        .param("isSubmit", isSubmit)
                        .param("isMember", isMember)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.members[1].name").value("masterHong"));
    }
}
