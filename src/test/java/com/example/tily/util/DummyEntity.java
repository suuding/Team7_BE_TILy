package com.example.tily.util;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.til.Til;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DummyEntity {
    protected User newUser(String email, String name, PasswordEncoder passwordEncoder){
        return User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode("hongHong!"))
                .role(Role.ROLE_USER)
                .build();
    }

    protected Roadmap newIndividualRoadmap(String creator, String category, String name, Long stepNum){
        return Roadmap.builder()
                .creator(creator)
                .category(category)
                .name(name)
                .stepNum(stepNum)
                .build();
    }

    protected Roadmap newGroupRoadmap(String creator, String category, String name, String description, boolean isPublic, Long currentNum, String code, boolean isRecruit,Long stepNum){
        return Roadmap.builder()
                .creator(creator)
                .category(category)
                .name(name)
                .description(description)
                .isPublic(isPublic)
                .currentNum(currentNum)
                .code(code)
                .isRecruit(isRecruit)
                .stepNum(stepNum)
                .build();
    }

    protected Step newIndividualStep(Roadmap roadmap, String title){
        return Step.builder()
                .roadmap(roadmap)
                .title(title)
                .build();
    }

    protected Step newGroupStep(Roadmap roadmap, String title, String description , LocalDateTime dueDate){
        return Step.builder()
                .roadmap(roadmap)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .build();
    }

    protected Til newTil(Step step, String title, String content, String submitContent, LocalDateTime submitDate, int commentNum){
        return Til.builder()
                .step(step)
                .title(title)
                .content(content)
                .submitContent(submitContent)
                .submitDate(submitDate)
                .commentNum(commentNum)
                .build();
    }

    protected List<Roadmap> individualRoadmapDummyList(){
        return Arrays.asList(
                newIndividualRoadmap("hong","individual", "스프링 시큐리티", 10L),
                newIndividualRoadmap("puuding","individual", "JPA 입문", 10L),
                newIndividualRoadmap("sam-mae","individual", "자바 reflection", 10L)
        );
    }

    protected List<Roadmap> groupRoadmapDummyList(){
        return Arrays.asList(
                newGroupRoadmap("hong", "group", "JAVA 입문 수업 - 생활 코딩", "생활 코딩님의 로드맵입니다!", true, 3L, "pnu1234", true, 3L),
                newGroupRoadmap("puuding", "group", "JPA 스터디", "김영한 강사님의 JPA를 공부하는 스터디 ^^", false, 10L, "ashfkc", true, 10L)
        );
    }

    protected List<Step> individualStepDummyList(List<Roadmap> roadmapListPS){
        return Arrays.asList(
                newIndividualStep(roadmapListPS.get(0), "알고리즘 Day1"),
                newIndividualStep(roadmapListPS.get(0), "알고리즘 Day2"),
                newIndividualStep(roadmapListPS.get(0), "알고리즘 Day3")
        );
    }

    protected List<Step> groupStepDummyList(List<Roadmap> roadmapListPS){
        return Arrays.asList(
                newGroupStep(roadmapListPS.get(0), "JPA - step1" ,"JPA란?" ,LocalDateTime.of(2023, 10, 1, 23 ,59)),
                newGroupStep(roadmapListPS.get(0), "JPA - step2" ,"JPA 사용해보기?" ,LocalDateTime.of(2023, 10, 2, 23 ,59)),
                newGroupStep(roadmapListPS.get(0), "JPA - step3" ,"JPA 코드 예제?" ,LocalDateTime.of(2023, 10, 3, 23 ,59))
        );
    }

    protected List<Til> newTil(List<Step> stepListPS){
        return Arrays.asList(
                newTil(stepListPS.get(0), "JPA 단위 테스트 1", "BDD (Behavior Driven Development) 패턴은..", "BDD (Behavior Driven Development) 패턴은~", LocalDateTime.of(2023, 10, 3, 23 ,59), 4),
                newTil(stepListPS.get(0), "JPA 단위 테스트 2", "@DataJpaTest은 스프링에서 JPA 단위 테스트를 진행할 때 사용되는 어노테이션..", "@DataJpaTest은 스프링에서 JPA 단위 테스트를 진행할 때 사용되는 어노테이션..", LocalDateTime.of(2023, 10, 6, 23 ,59), 5),
                newTil(stepListPS.get(0), "JPA 단위 테스트 3", "슬라이스 테스트를 지원하며, JPA 관련 구성만 로드해 애플리케이션의 전체를 로드하는 것보다 테스트 실행 시간을 줄일..", "슬라이스 테스트를 지원하며, JPA 관련 구성만 로드해 애플리케이션의 전체를 로드하는 것보다 테스트 실행 시간을 줄일..", LocalDateTime.of(2023, 10, 10, 23 ,59), 5)
        );
    }
}
