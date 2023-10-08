package com.example.tily;

import com.example.tily.roadmap.Category;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import com.example.tily.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@EnableJpaAuditing
@SpringBootApplication
public class TiLyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiLyApplication.class, args);
	}

	@Profile("local")
	@Bean
	CommandLineRunner localServerStart(UserRepository userRepository, RoadmapRepository roadmapRepository, StepRepository stepRepository, ReferenceRepository referenceRepository, TilRepository tilRepository, PasswordEncoder passwordEncoder,
									   UserRoadmapRepository userRoadmapRepository) {
		return args -> {
			userRepository.saveAll(Arrays.asList(
					newUser("tngus@test.com", "su", passwordEncoder, Role.ROLE_USER),
					newUser("hong@naver.com", "hong", passwordEncoder, Role.ROLE_USER),
					newUser("test@test.com", "test", passwordEncoder, Role.ROLE_USER),
					newUser("admin@test.com", "admin", passwordEncoder, Role.ROLE_ADMIN)
			));
			roadmapRepository.saveAll(Arrays.asList(
					newIndividualRoadmap(User.builder().id(1L).build(), Category.CATEGORY_INDIVIDUAL, "스프링 시큐리티", 10L), //1
					newIndividualRoadmap(User.builder().id(2L).build(),Category.CATEGORY_INDIVIDUAL, "JPA 입문", 10L),
					newIndividualRoadmap(User.builder().id(3L).build(),Category.CATEGORY_INDIVIDUAL, "자바 reflection", 10L),

					newTilyRoadmap(User.builder().id(4L).build(), Category.CATEGORY_TILY, "spring boot - 초급편", "틸리에서 제공하는 spring boot 초급자를 위한 로드맵입니다.", 100L, 20L, "image.jpg"), //4
					newTilyRoadmap(User.builder().id(4L).build(), Category.CATEGORY_TILY, "spring boot - 중급편", "틸리에서 제공하는 spring boot 중급자를 위한 로드맵입니다.", 80L, 30L , "image.jpg"),
					newTilyRoadmap(User.builder().id(4L).build(), Category.CATEGORY_TILY, "spring boot - 고급편", "틸리에서 제공하는 spring boot 고급자를 위한 로드맵입니다.", 50L, 30L , "image.jpg"),
					newTilyRoadmap(User.builder().id(4L).build(), Category.CATEGORY_TILY, "Spring JPA - 초급편", "틸리에서 제공하는 Spring JPA 초급자를 위한 로드맵입니다.", 100L, 20L , "image.jpg"),
					newTilyRoadmap(User.builder().id(4L).build(), Category.CATEGORY_TILY, "Spring JPA - 중급편", "틸리에서 제공하는 Spring JPA 중급자를 위한 로드맵입니다.", 80L, 20L , "image.jpg"),
					newTilyRoadmap(User.builder().id(4L).build(), Category.CATEGORY_TILY, "Spring JPA - 고급편", "틸리에서 제공하는 Spring JPA 고급자를 위한 로드맵입니다.", 50L, 20L , "image.jpg"),

					newGroupRoadmap(User.builder().id(1L).build(), Category.CATEGORY_GROUP, "JAVA 입문 수업 - 생활 코딩", "생활 코딩님의 로드맵입니다!", true, 5L, "pnu1234", true, 10L), // 10
					newGroupRoadmap(User.builder().id(1L).build(), Category.CATEGORY_GROUP, "spring boot 스터디", "같이 spring boot 공부해봐요!", true, 3L, "pnu1234", true, 10L),
					newGroupRoadmap(User.builder().id(2L).build(), Category.CATEGORY_GROUP, "JPA 스터디", "김영한 강사님의 JPA를 공부하는 스터디 ^^", false, 5L, "ashfkc", false, 20L),
					newGroupRoadmap(User.builder().id(2L).build(), Category.CATEGORY_GROUP, "데이터베이스 스터디", "데이터베이스 공부합시다", true, 8L, "asdf", true, 15L),
					newGroupRoadmap(User.builder().id(3L).build(), Category.CATEGORY_GROUP, "카카오테크캠퍼스 1단계", "카카오테크캠퍼스 1단계입니다.", false, 50L, "kakao", true, 20L)

					));
			userRoadmapRepository.saveAll(Arrays.asList(
					newUserRoadmapRelation(Roadmap.builder().id(1L).build(), User.builder().id(1L).build(), null, null, "master", 0),
					newUserRoadmapRelation(Roadmap.builder().id(2L).build(), User.builder().id(2L).build(), null, null, "master", 0),
					newUserRoadmapRelation(Roadmap.builder().id(3L).build(), User.builder().id(3L).build(), null, null, "master", 0),

					newUserRoadmapRelation(Roadmap.builder().id(4L).build(), User.builder().id(4L).build(), null, true, "master", 10),
					newUserRoadmapRelation(Roadmap.builder().id(4L).build(), User.builder().id(1L).build(), null, true, "member", 10),
					newUserRoadmapRelation(Roadmap.builder().id(4L).build(), User.builder().id(2L).build(), null, true, "member", 20),
					newUserRoadmapRelation(Roadmap.builder().id(4L).build(), User.builder().id(3L).build(), null, true, "member", 100),
					newUserRoadmapRelation(Roadmap.builder().id(5L).build(), User.builder().id(4L).build(), null, true, "master", 0),
					newUserRoadmapRelation(Roadmap.builder().id(6L).build(), User.builder().id(4L).build(), null, true, "master", 0),
					newUserRoadmapRelation(Roadmap.builder().id(7L).build(), User.builder().id(4L).build(), null, true, "master", 0),
					newUserRoadmapRelation(Roadmap.builder().id(7L).build(), User.builder().id(1L).build(), null, true, "member", 100),
					newUserRoadmapRelation(Roadmap.builder().id(7L).build(), User.builder().id(2L).build(), null, true, "member", 20),
					newUserRoadmapRelation(Roadmap.builder().id(7L).build(), User.builder().id(3L).build(), null, true, "member", 100),
					newUserRoadmapRelation(Roadmap.builder().id(8L).build(), User.builder().id(4L).build(), null, true, "master", 0),
					newUserRoadmapRelation(Roadmap.builder().id(9L).build(), User.builder().id(4L).build(), null, true, "master", 0),

					newUserRoadmapRelation(Roadmap.builder().id(10L).build(), User.builder().id(2L).build(), "자바 공부하고싶습니다!", true, "member", 10),
					newUserRoadmapRelation(Roadmap.builder().id(10L).build(), User.builder().id(3L).build(), "자바 공부하고싶습니다!", true, "member", 10),
					newUserRoadmapRelation(Roadmap.builder().id(12L).build(), User.builder().id(1L).build(), "열심히 하겠습니다!", true, "member", 0),
					newUserRoadmapRelation(Roadmap.builder().id(12L).build(), User.builder().id(3L).build(), "열심히 하겠습니다!", true, "member", 0),
					newUserRoadmapRelation(Roadmap.builder().id(13L).build(), User.builder().id(1L).build(), "열심히 하겠습니다!", false, "none", 20)
					));
			stepRepository.saveAll(Arrays.asList(
					newIndividualStep(Roadmap.builder().id(1L).build(), "스프링 시큐리티를 사용하는 이유"),
					newIndividualStep(Roadmap.builder().id(1L).build(), "OAuth 2.0으로 로그인 기능 구현하기"),
					newIndividualStep(Roadmap.builder().id(1L).build(), "인증된 사용자 권한 부족 예외처리"),
					newIndividualStep(Roadmap.builder().id(1L).build(), "소셜 로그인 사용하기"),

					newGroupStep(Roadmap.builder().id(12L).build(),"다형성(Polymorphism)", "Day1", LocalDateTime.of(2023, 10, 1, 23 ,59) ),
					newGroupStep(Roadmap.builder().id(12L).build(),"람다식(lambda expression)", "Day2", LocalDateTime.of(2023, 10, 3, 23 ,59) ),
					newGroupStep(Roadmap.builder().id(12L).build(),"스트림(lambda expression)", "Day3", LocalDateTime.of(2023, 10, 5, 23 ,59) )
			));
			referenceRepository.saveAll(Arrays.asList(
					newReference(Step.builder().id(4L).build(), "youtube", "https://www.youtube.com/watch?v=0L6QWKC1a6k"),
					newReference(Step.builder().id(4L).build(), "youtube", "https://www.youtube.com/watch?v=U8LVCTaS3mQ"),
					newReference(Step.builder().id(6L).build(), "youtube", "https://www.youtube.com/watch?v=1OLy4Dj_zCg"),
					newReference(Step.builder().id(4L).build(), "web", "https://blog.naver.com/hoyai-/1234"),
					newReference(Step.builder().id(5L).build(), "web", "https://blog.naver.com/cestlavie_01/1234"),
					newReference(Step.builder().id(5L).build(), "web", "https://velog.io/@skydoves/open-source-machenism")
			));
			tilRepository.saveAll(Arrays.asList(
					newTil(Roadmap.builder().id(1L).build(), Step.builder().id(1L).build(), User.builder().id(1L).build(), "스프링 시큐리티를 사용하는 이유", "이것은 내용입니다.", true, null),
					newTil(Roadmap.builder().id(1L).build(), Step.builder().id(2L).build(), User.builder().id(1L).build(), "OAuth 2.0으로 로그인 기능 구현하기", "이것은 내용입니다.", true, null),
					newTil(Roadmap.builder().id(1L).build(), Step.builder().id(3L).build(), User.builder().id(1L).build(), "인증된 사용자 권한 부족 예외처리", "이것은 내용입니다.", true, null),
					newTil(Roadmap.builder().id(1L).build(), Step.builder().id(4L).build(), User.builder().id(1L).build(), "소셜 로그인 사용하기", "이것은 내용입니다.", true, null),

					newTil(Roadmap.builder().id(12L).build(), Step.builder().id(5L).build(), User.builder().id(1L).build(), "다형성(Polymorphism)", "이것은 내용입니다.", false, "이것은 제출할 내용입니다."),
					newTil(Roadmap.builder().id(12L).build(), Step.builder().id(6L).build(), User.builder().id(1L).build(), "람다식(lambda expression)", "이것은 내용입니다.", false, "이것은 제출할 내용입니다."),
					newTil(Roadmap.builder().id(12L).build(), Step.builder().id(7L).build(), User.builder().id(1L).build(), "스트림(lambda expression)", "이것은 내용입니다.", false, "이것은 제출할 내용입니다.")

			));
		};
	}

	private User newUser(String email, String name, PasswordEncoder passwordEncoder, Role role){
		return User.builder()
				.email(email)
				.name(name)
				.password(passwordEncoder.encode("hongHong!"))
				.role(role)
				.build();
	}

	private Roadmap newIndividualRoadmap(User creator, Category category, String name, Long stepNum){
		return Roadmap.builder()
				.creator(creator)
				.category(category)
				.name(name)
				.stepNum(stepNum)
				.build();
	}

	public Roadmap newTilyRoadmap(User creator, Category category, String name, String description, Long currentNum, Long stepNum, String image) {
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

	private Roadmap newGroupRoadmap(User creator, Category category, String name, String description, boolean isPublic, Long currentNum, String code, boolean isRecruit,Long stepNum) {
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

	private UserRoadmap newUserRoadmapRelation(Roadmap roadmap, User user, String content, Boolean isAccept, String role, int progress) {
		return UserRoadmap.builder()
				.roadmap(roadmap)
				.user(user)
				.content(content)
				.isAccept(isAccept)
				.role(role)
				.progress(progress)
				.build();
	}

	private Step newIndividualStep(Roadmap roadmap, String title) {
		return Step.builder()
				.roadmap(roadmap)
				.title(title)
				.build();
	}

	private Step newGroupStep(Roadmap roadmap, String title, String description, LocalDateTime dueDate){
		return Step.builder()
				.roadmap(roadmap)
				.title(title)
				.description(description)
				.dueDate(dueDate)
				.build();
	}

	private Reference newReference(Step step, String category, String link){
		return Reference.builder()
				.step(step)
				.category(category)
				.link(link)
        		.build();
  	}
  
	private Til newTil(Roadmap roadmap, Step step, User writer, String title, String content, boolean isPersonal, String subContent) {
		return Til.builder()
				.roadmap(roadmap)
				.step(step)
				.writer(writer)
				.title(title)
				.content(content)
				.isPersonal(isPersonal)
				.submitContent(subContent)
				.build();
	}
}
