package com.example.tily.roadmap;

import com.example.tily.roadmap.relation.GroupRole;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("local")
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

        String category = "individual";
        String description = "알고리즘 마무리하는 로드맵입니다.";
        Boolean isPublic = false;

        RoadmapRequest.CreateRoadmapDTO requestDTO = new RoadmapRequest.CreateRoadmapDTO(category,name,description,isPublic);


        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }


    @DisplayName("그룹 로드맵_생성_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_create_success_test() throws Exception {

        // given

        String name = "hong";
        String category = "group";
        String description = "알고리즘 마무리하는 로드맵입니다.";
        boolean isPublic = true;

        RoadmapRequest.CreateRoadmapDTO requestDTO = new RoadmapRequest.CreateRoadmapDTO(name,category,description,isPublic);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("개인 로드맵_생성_실패_test: 이름 미입력")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_create_fail_test1() throws Exception {

        // given
        String name = "";
        String category = "group";
        String description = "알고리즘 마무리하는 로드맵입니다.";
        boolean isPublic = false;
        RoadmapRequest.CreateRoadmapDTO requestDTO = new RoadmapRequest.CreateRoadmapDTO(category, name, description, isPublic);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));

    }


    // 실패 케이스는 화면을 바탕으로 만듦
    @DisplayName("로드맵_생성_실패_test: 카테고리 입력하지 않음")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_create_fail_test2() throws Exception {
        // given
        String name = "알고리즘";
        String category = "";
        String description = "알고리즘 마무리하는 로드맵입니다.";
        boolean isPublic = false;
        RoadmapRequest.CreateRoadmapDTO requestDTO = new RoadmapRequest.CreateRoadmapDTO(name, category, description, isPublic);

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps")
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
                get("/api/roadmaps/"+ id)
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

        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("그룹 로드맵_조회_실패_test: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_group_find_fail_test1() throws Exception {
        // given
        Long roadmapId = 20L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/"+ roadmapId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("그룹 로드맵_수정_성공_test")
    @WithUserDetails(value = "admin@test.com")
    @Test
    public void roadmap_group_update_success_test() throws Exception {
        // given
        Long id = 4L;

        // step1의 참조 - youtube
        List<RoadmapRequest.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.ReferenceDTO youtubeReference1 = new RoadmapRequest.ReferenceDTO(1L, "https://www.youtube.com/watch?v=수정된 주소");
        youtubeReferences.add(youtubeReference1);

        // step1의 참조 - web
        List<RoadmapRequest.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.ReferenceDTO webReference1 = new RoadmapRequest.ReferenceDTO(4L, "https://blog.naver.com/hoyai-/수정된 주소");
        webReferences.add(webReference1);

        // step1의 참조
        RoadmapRequest.ReferenceDTOs references1 = new RoadmapRequest.ReferenceDTOs(youtubeReferences, webReferences);

        // 스텝
        RoadmapRequest.StepDTO step1 = new RoadmapRequest.StepDTO(4L, "다형성(Polymorphism)", "수정된 step description 입니다", references1, null);
        List<RoadmapRequest.StepDTO> steps = new ArrayList<>();
        steps.add(step1);

        // 로드맵
        RoadmapRequest.RoadmapDTO roadmap = new RoadmapRequest.RoadmapDTO("new JAVA - 생활 코딩", "새로운 버젼 입니다", "modifiedCode1234", false, true);


        RoadmapRequest.UpdateRoadmapDTO requestDTO = new RoadmapRequest.UpdateRoadmapDTO("생활 코딩","새로운 버젼 입니다", true , true );

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    // 실패 케이스는 화면을 바탕으로 만듦
    @DisplayName("그룹 로드맵_수정_실패1_test: 로드맵 이름을 입력하지 않음")
    @WithUserDetails(value = "admin@test.com")
    @Test
    public void roadmap_group_update_fail_test_1() throws Exception {
        // given
        Long id = 4L;

        // step1의 참조 - youtube
        List<RoadmapRequest.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.ReferenceDTO youtubeReference1 = new RoadmapRequest.ReferenceDTO(1L, "https://www.youtube.com/watch?v=수정된 주소");
        youtubeReferences.add(youtubeReference1);

        // step1의 참조 - web
        List<RoadmapRequest.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.ReferenceDTO webReference1 = new RoadmapRequest.ReferenceDTO(4L, "https://blog.naver.com/hoyai-/수정된 주소");
        webReferences.add(webReference1);

        // step1의 참조
        RoadmapRequest.ReferenceDTOs references1 = new RoadmapRequest.ReferenceDTOs(youtubeReferences, webReferences);

        // 스텝
        RoadmapRequest.StepDTO step1 = new RoadmapRequest.StepDTO(4L, "다형성(Polymorphism)", "수정된 step description 입니다", references1, null);
        List<RoadmapRequest.StepDTO> steps = new ArrayList<>();
        steps.add(step1);

        // 로드맵
        RoadmapRequest.RoadmapDTO roadmap = new RoadmapRequest.RoadmapDTO(null, "새로운 버젼 입니다", "modifiedCode1234", false, true);


        RoadmapRequest.UpdateRoadmapDTO requestDTO = new RoadmapRequest.UpdateRoadmapDTO(null,"새로운 버젼 입니다", true, true );


        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("그룹 로드맵_수정_실패2_test: 권한 없는 접근")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void roadmap_group_update_fail_test_2() throws Exception {
        // given
        Long id = 4L;

        // step1의 참조 - youtube
        List<RoadmapRequest.ReferenceDTO> youtubeReferences = new ArrayList<>();

        RoadmapRequest.ReferenceDTO youtubeReference1 = new RoadmapRequest.ReferenceDTO(1L, "https://www.youtube.com/watch?v=수정된 주소");
        youtubeReferences.add(youtubeReference1);

        // step1의 참조 - web
        List<RoadmapRequest.ReferenceDTO> webReferences = new ArrayList<>();

        RoadmapRequest.ReferenceDTO webReference1 = new RoadmapRequest.ReferenceDTO(4L, "https://blog.naver.com/hoyai-/수정된 주소");
        webReferences.add(webReference1);

        // step1의 참조
        RoadmapRequest.ReferenceDTOs references1 = new RoadmapRequest.ReferenceDTOs(youtubeReferences, webReferences);

        // 스텝
        RoadmapRequest.StepDTO step1 = new RoadmapRequest.StepDTO(4L, "다형성(Polymorphism)", "수정된 step description 입니다", references1, null);
        List<RoadmapRequest.StepDTO> steps = new ArrayList<>();
        steps.add(step1);

        // 로드맵
        RoadmapRequest.RoadmapDTO roadmap = new RoadmapRequest.RoadmapDTO("new JAVA - 생활 코딩", "새로운 버젼 입니다", "modifiedCode1234", false, true);

        RoadmapRequest.UpdateRoadmapDTO requestDTO = new RoadmapRequest.UpdateRoadmapDTO(roadmap.name(), roadmap.description(), roadmap.isPublic(), roadmap.isRecruit());

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/roadmaps/"+ id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 접근할 권한이 없습니다."));
    }

    @DisplayName("내가 속한 로드맵 전체 목록_조회_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void roadmap_my_find_success_test () throws Exception {

        // given

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/my")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.categories[0].id").value(1L));
        result.andExpect(jsonPath("$.result.roadmaps.tilys[0].id").value(4L));
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
                get("/api/roadmaps")
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


    @DisplayName("그룹로드맵_신청하기_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void group_roadmap_apply_success_test() throws Exception{
        // given
        Long id = 10L;

        RoadmapRequest.ApplyRoadmapDTO requestDTO = new RoadmapRequest.ApplyRoadmapDTO("안녕하세요, 반갑습니다~");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/" + id + "/apply")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)

        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("로드맵_신청하기_실패_test1: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void group_roadmap_apply_fail_test_1() throws Exception{
        // given
        Long id = 20L;

        RoadmapRequest.ApplyRoadmapDTO requestDTO = new RoadmapRequest.ApplyRoadmapDTO("안녕하세요, 반갑습니다~");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/" + id + "/apply")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("로드맵_신청하기_실패_test2: 이미 가입한 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void group_roadmap_apply_fail_test_2() throws Exception{
        // given
        Long id = 10L;

        RoadmapRequest.ApplyRoadmapDTO requestDTO = new RoadmapRequest.ApplyRoadmapDTO("안녕하세요, 반갑습니다~");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/" + id + "/apply")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("이미 해당 로드맵에 속했습니다."));
    }

    @DisplayName("그룹로드맵_참여하기_성공_test")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void group_roadmap_participate_success_test() throws Exception{
        // given
        RoadmapRequest.ParticipateRoadmapDTO requestDTO = new RoadmapRequest.ParticipateRoadmapDTO("pnu12345");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/participate")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("그룹로드맵_참여하기_실패_test1: 존재하지 않은 로드맵")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void group_roadmap_participate_fail_test_1() throws Exception{
        // given
        RoadmapRequest.ParticipateRoadmapDTO requestDTO = new RoadmapRequest.ParticipateRoadmapDTO("pnu12347");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/participate")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("그룹로드맵_참여하기_실패_test2: 이미 속한 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void group_roadmap_participate_fail_test_2() throws Exception{
        // given
        RoadmapRequest.ParticipateRoadmapDTO requestDTO = new RoadmapRequest.ParticipateRoadmapDTO("pnu12345");

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/participate")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("이미 해당 로드맵에 속했습니다."));
    }

    @DisplayName("틸리로드맵_참여하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void tily_roadmap_participate_success_test() throws Exception{

        // given
        Long id = 5L;

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/tily/" + id+ "/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("틸리로드맵_참여하기_실패_test1: 존재하지 않는 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void tily_roadmap_participate_fail_test_1() throws Exception{

        // given
        Long id = 112L;

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/tily/" + id+ "/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("틸리로드맵_참여하기_실패_test2: 이미 속한 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void tily_roadmap_participate_fail_test_2() throws Exception{

        // given
        Long id = 4L;

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/tily/" + id+ "/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("이미 해당 로드맵에 속했습니다."));
    }

    @DisplayName("로드맵_구성원_전체조회_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_members_find_success_test() throws Exception{
        // given
        Long id = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+id+"/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        result.andExpect(jsonPath("$.result.users[0].name").value("hong"));
        result.andExpect(jsonPath("$.result.myRole").value("manager"));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("로드맵_구성원_전체조회_실패_test1: 권한 없음")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void roadmap_members_find_fail_test_1() throws Exception{
        // given
        Long id = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+id+"/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 접근할 권한이 없습니다."));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("로드맵_구성원_전체조회_실패_test2: 해당 로드맵에 속하지 않은 사람의 접근")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void roadmap_members_find_fail_test_2() throws Exception{
        // given
        Long id = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+id+"/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 속하지 않습니다."));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("로드맵_구성원_전체조회_실패_test3: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void roadmap_members_find_fail_test3() throws Exception{
        // given
        Long id = 2444L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+ id +"/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("구성원_역할_변경하기_성공_test")
    @WithUserDetails(value = "admin@test.com")
    @Test
    public void member_role_change_success_test() throws Exception{
        // given
        Long groupsId = 4L;
        Long usersId = 2L;
        RoadmapRequest.ChangeMemberRoleDTO requestDTO = new RoadmapRequest.ChangeMemberRoleDTO(GroupRole.ROLE_MANAGER.getValue());

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/roadmaps/groups/"+ groupsId +"/members/"+ usersId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("구성원_역할_변경하기_실패_test1: 권한 없는 유저")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void member_role_change_fail_test_1() throws Exception{
        // given
        Long groupsId = 4L;
        Long usersId = 1L;
        RoadmapRequest.ChangeMemberRoleDTO requestDTO = new RoadmapRequest.ChangeMemberRoleDTO(GroupRole.ROLE_MANAGER.getValue());

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/roadmaps/groups/"+ groupsId +"/members/"+ usersId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("master 권한이 필요합니다."));
    }

    @DisplayName("구성원_역할_변경하기_실패_test2: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hoyai@naver.com")
    @Test
    public void member_role_change_fail_test_2() throws Exception{
        // given
        Long groupsId = 20L;
        Long usersId = 2L;
        RoadmapRequest.ChangeMemberRoleDTO requestDTO = new RoadmapRequest.ChangeMemberRoleDTO(GroupRole.ROLE_MANAGER.getValue());

        String requestBody = om.writeValueAsString(requestDTO);

        // when
        ResultActions result = mvc.perform(
                patch("/api/roadmaps/groups/"+ groupsId +"/members/"+ usersId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
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
                delete("/api/roadmaps/groups/" + groupsId + "/members/" + usersId)
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
                delete("/api/roadmaps/groups/" + groupsId + "/members/" + usersId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 속하지 않습니다."));
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
                delete("/api/roadmaps/groups/" + groupsId + "/members/" + usersId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("구성원_강퇴하기_실패_test3: 강퇴 권한 없는 유저")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void member_dismiss_fail_test_3() throws Exception {
        // given
        Long groupsId = 12L;
        Long usersId = 5L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/roadmaps/groups/" + groupsId + "/members/" + usersId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 접근할 권한이 없습니다."));
    }

    @DisplayName("신청자_조회하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void applied_user_find_success_test() throws Exception {
        // given
        Long groupsId = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+ groupsId +"/members/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
        //result.andExpect(jsonPath("$.result.users[0].name").value("applier"));
        //result.andExpect(jsonPath("$.result.users[0].content").value("참가 신청합니다"));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
    }

    @DisplayName("신청자_조회하기_실패_test: 존재하지 않는 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void applied_user_find_fail_test_1() throws Exception {
        // given
        Long groupsId = 20L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+ groupsId +"/members/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("신청자_조회하기_실패_test2: 로드맵에 속하지 않은 유저의 접근")
    @WithUserDetails(value = "tngus@test.com")
    @Test
    public void applied_user_find_fail_test_2() throws Exception {
        // given
        Long groupsId = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+ groupsId +"/members/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 속하지 않습니다."));
    }

    @DisplayName("신청자_조회하기_실패_test3: 권한 없는 멤버의 접근")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void applied_user_find_fail_test_3() throws Exception {
        // given
        Long groupsId = 10L;

        // when
        ResultActions result = mvc.perform(
                get("/api/roadmaps/groups/"+ groupsId +"/members/apply")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 접근할 권한이 없습니다."));
    }

    @DisplayName("신청_승인하기_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_accept_success_test() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 7L;

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

//    @DisplayName("신청_승인하기_실패_test1: 존재하지 않은 유저")
//    @WithUserDetails(value = "hong@naver.com")
//    @Test
//    public void application_accept_fail_test_1() throws Exception {
//        // given
//        Long groupsId = 5L;
//        Long membersId = 10L;
//
//        // when
//        ResultActions result = mvc.perform(
//                post("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//        );
//
//        // then
//        result.andExpect(jsonPath("$.success").value("false"));
//        result.andExpect(jsonPath("$.message").value("해당 사용자를 찾을 수 없습니다."));
//    }

    @DisplayName("신청_승인하기_실패_test1: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_accept_fail_test_1() throws Exception {
        // given
        Long groupsId = 20L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("신청_승인하기_실패_test2: 권한이 없는 유저")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void application_accept_fail_test_2() throws Exception {
        // given
        Long groupsId = 5L;
        Long membersId = 6L;

        // when
        ResultActions result = mvc.perform(
                post("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/accept")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 속하지 않습니다."));
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
                delete("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("true"));
    }

    @DisplayName("신청_거절하기_실패_test1: 존재하지 않은 유저")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_reject_fail_test_1() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 사용자를 찾을 수 없습니다."));
    }

    @DisplayName("신청_거절하기_실패_test2: 존재하지 않은 로드맵")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void application_reject_fail_test_2() throws Exception {
        // given
        Long groupsId = 20L;
        Long membersId = 10L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap을 찾을 수 없습니다."));
    }

    @DisplayName("신청_거절하기_실패_test3: 권한 없는 유저")
    @WithUserDetails(value = "test@test.com")
    @Test
    public void application_reject_fail_test_3() throws Exception {
        // given
        Long groupsId = 10L;
        Long membersId = 6L;

        // when
        ResultActions result = mvc.perform(
                delete("/api/roadmaps/groups/"+ groupsId +"/members/"+ membersId +"/reject")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        result.andExpect(jsonPath("$.success").value("false"));
        result.andExpect(jsonPath("$.message").value("해당 roadmap에 접근할 권한이 없습니다."));
    }
    /*
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
    */


    @DisplayName("그룹_로드맵_삭제_성공_test")
    @WithUserDetails(value = "hong@naver.com")
    @Test
    public void group_roadmap_delete_success_test() throws Exception {

        //given
        Long groupsId = 10L;

        //when
        ResultActions result = mvc.perform(
                delete("/api/roadmaps/" + groupsId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(jsonPath("$.success").value("true"));
    }
}
