package com.example.tily.util;

import com.example.tily.roadmap.Category;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.step.Step;
import com.example.tily.til.Til;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DummyEntity {
    protected User newUser(String email, String name, Role role){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode("hongHong!"))
                .role(role)
                .build();
    }

    protected Roadmap newIndividualRoadmap(User creator, Category category, String name, int stepNum){
        return Roadmap.builder()
                .creator(creator)
                .category(category)
                .name(name)
                .stepNum(stepNum)
                .build();
    }

    public Roadmap newTilyRoadmap(User creator, Category category, String name, String description, Long currentNum, int stepNum, String image) {
        return Roadmap.builder()
                .creator(creator)
                .category(category)
                .name(name)
                .description(description)
                .currentNum(currentNum)
                .stepNum(stepNum)
                .image(image)
                .build();
    }

    protected Roadmap newGroupRoadmap(User creator, Category category, String name, String description, boolean isPublic, Long currentNum, String code, boolean isRecruit, int stepNum){
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

    private UserRoadmap newUserRoadmapRelation(Roadmap roadmap, User user, String content, Boolean isAccept, GroupRole role, int progress) {
        return UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .content(content)
                .isAccept(isAccept)
                .role(role)
                .progress(progress)
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

    protected List<User> userDummyList() {
        return Arrays.asList(
                newUser("tngus@test.com", "su", Role.ROLE_USER),
                newUser("hong@naver.com", "hong", Role.ROLE_USER),
                newUser("test@test.com", "hongHong", Role.ROLE_USER),
                newUser("admin@test.com", "admin", Role.ROLE_ADMIN)
        );
    }
    protected List<Roadmap> roadmapDummyList(List<User> userListPS){
        return Arrays.asList(
                newIndividualRoadmap(userListPS.get(0), Category.CATEGORY_INDIVIDUAL, "스프링 시큐리티", 10),
                newIndividualRoadmap(userListPS.get(1),Category.CATEGORY_INDIVIDUAL, "JPA 입문", 10),
                newIndividualRoadmap(userListPS.get(2),Category.CATEGORY_INDIVIDUAL, "자바 reflection", 10),
                newTilyRoadmap(userListPS.get(3), Category.CATEGORY_TILY, "spring boot - 초급편", "틸리에서 제공하는 spring boot 초급자를 위한 로드맵입니다.", 100L, 20, "image.jpg"),
                newTilyRoadmap(userListPS.get(3), Category.CATEGORY_TILY, "spring boot - 중급편", "틸리에서 제공하는 spring boot 중급자를 위한 로드맵입니다.", 80L, 30 , "image.jpg"),
                newGroupRoadmap(userListPS.get(0), Category.CATEGORY_GROUP, "JAVA 입문 수업 - 생활 코딩", "생활 코딩님의 로드맵입니다!", true, 3L, "pnu1234", true, 3),
                newGroupRoadmap(userListPS.get(1), Category.CATEGORY_GROUP, "JPA 스터디", "김영한 강사님의 JPA를 공부하는 스터디 ^^", false, 10L, "ashfkc", true, 10)
        );
    }

    protected List<UserRoadmap> userRoadmapDummyList(List<Roadmap> roadmapListPS, List<User> userListPS) {
        return Arrays.asList(
                newUserRoadmapRelation(roadmapListPS.get(0), userListPS.get(0), null, null, GroupRole.ROLE_MASTER, 0),
                newUserRoadmapRelation(roadmapListPS.get(1), userListPS.get(1), null, null, GroupRole.ROLE_MASTER, 0),
                newUserRoadmapRelation(roadmapListPS.get(2), userListPS.get(2), null, null, GroupRole.ROLE_MASTER, 0),
                newUserRoadmapRelation(roadmapListPS.get(3), userListPS.get(3), null, true, GroupRole.ROLE_MASTER, 0),
                newUserRoadmapRelation(roadmapListPS.get(4), userListPS.get(3), null, true, GroupRole.ROLE_MASTER, 0),
                newUserRoadmapRelation(roadmapListPS.get(4), userListPS.get(1), null, true, GroupRole.ROLE_MEMBER, 20),
                newUserRoadmapRelation(roadmapListPS.get(4), userListPS.get(2), null, true, GroupRole.ROLE_MEMBER, 100),
                newUserRoadmapRelation(roadmapListPS.get(5), userListPS.get(0), null, true, GroupRole.ROLE_MASTER, 10),
                newUserRoadmapRelation(roadmapListPS.get(5), userListPS.get(1), "자바 공부하고싶습니다!", true, GroupRole.ROLE_MEMBER, 10),
                newUserRoadmapRelation(roadmapListPS.get(5), userListPS.get(2), "자바 공부하고싶습니다!", true, GroupRole.ROLE_MEMBER, 10),
                newUserRoadmapRelation(roadmapListPS.get(6), userListPS.get(1), null, true, GroupRole.ROLE_MASTER, 10),
                newUserRoadmapRelation(roadmapListPS.get(6), userListPS.get(0), "자바 공부하고싶습니다!", true, GroupRole.ROLE_MEMBER, 0),
                newUserRoadmapRelation(roadmapListPS.get(6), userListPS.get(2), "자바 공부하고싶습니다!", null, GroupRole.ROLE_NONE, 0)
        );
    }

    protected List<Step> stepDummyList(List<Roadmap> roadmapListPS){
        return Arrays.asList(
                newIndividualStep(roadmapListPS.get(0), "스프링 시큐리티를 사용하는 이유"),
                newIndividualStep(roadmapListPS.get(0), "OAuth 2.0으로 로그인 기능 구현하기"),
                newIndividualStep(roadmapListPS.get(0), "인증된 사용자 권한 부족 예외처리"),
                newGroupStep(roadmapListPS.get(3),"다형성(Polymorphism)", "Day1", LocalDateTime.of(2023, 10, 1, 23 ,59) ),
                newGroupStep(roadmapListPS.get(3),"람다식(lambda expression)", "Day2", LocalDateTime.of(2023, 10, 3, 23 ,59) ),
                newGroupStep(roadmapListPS.get(6),"스트림(lambda expression)", "Day1", LocalDateTime.of(2023, 10, 5, 23 ,59) ),
                newGroupStep(roadmapListPS.get(6),"스트림(lambda expression)2", "Day2", LocalDateTime.of(2023, 10, 10, 23 ,59) )
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
