package com.example.tily;

import com.example.tily.roadmap.Category;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;

@EnableJpaAuditing
@SpringBootApplication
public class TiLyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiLyApplication.class, args);
	}

	@Profile("local")
	@Bean
	CommandLineRunner localServerStart(UserRepository userRepository, RoadmapRepository roadmapRepository, StepRepository stepRepository, ReferenceRepository referenceRepository, TilRepository tilRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			userRepository.saveAll(Arrays.asList(newUser("hong@naver.com", "hong", passwordEncoder)));
			userRepository.saveAll(Arrays.asList(newUser("test@test.com", "hongHong", passwordEncoder)));
			roadmapRepository.saveAll(Arrays.asList(
					newIndividualRoadmap("hong",Category.CATEGORY_INDIVIDUAL, "스프링 시큐리티", 10L),
					newIndividualRoadmap("puuding",Category.CATEGORY_INDIVIDUAL, "JPA 입문", 10L),
					newIndividualRoadmap("sam-mae",Category.CATEGORY_INDIVIDUAL, "자바 reflection", 10L),
					newGroupRoadmap("hong", Category.CATEGORY_GROUP, "JAVA 입문 수업 - 생활 코딩", "생활 코딩님의 로드맵입니다!", true, 3L, "pnu1234", true, 3L),
					newGroupRoadmap("puuding", Category.CATEGORY_GROUP, "JPA 스터디", "김영한 강사님의 JPA를 공부하는 스터디 ^^", false, 10L, "ashfkc", true, 10L)
			));
			stepRepository.saveAll(Arrays.asList(
					newIndividualStep(Roadmap.builder().id(1L).build(), "스프링 시큐리티를 사용하는 이유"),
					newIndividualStep(Roadmap.builder().id(1L).build(), "OAuth 2.0으로 로그인 기능 구현하기"),
					newIndividualStep(Roadmap.builder().id(1L).build(), "인증된 사용자 권한 부족 예외처리"),
					newGroupStep(Roadmap.builder().id(4L).build(),"다형성(Polymorphism)", "Day1", LocalDateTime.of(2023, 10, 1, 23 ,59) ),
					newGroupStep(Roadmap.builder().id(4L).build(),"람다식(lambda expression)", "Day2", LocalDateTime.of(2023, 10, 3, 23 ,59) ),
					newGroupStep(Roadmap.builder().id(5L).build(),"스트림(lambda expression)", "Day3", LocalDateTime.of(2023, 10, 5, 23 ,59) )
			));
			referenceRepository.saveAll(Arrays.asList(
					newReference(Step.builder().id(4L).build(), "youtube", "https://www.youtube.com/watch?v=0L6QWKC1a6k"),
					newReference(Step.builder().id(5L).build(), "youtube", "https://www.youtube.com/watch?v=U8LVCTaS3mQ"),
					newReference(Step.builder().id(6L).build(), "youtube", "https://www.youtube.com/watch?v=1OLy4Dj_zCg"),
					newReference(Step.builder().id(4L).build(), "web", "https://blog.naver.com/hoyai-/1234"),
					newReference(Step.builder().id(5L).build(), "web", "https://blog.naver.com/cestlavie_01/1234"),
					newReference(Step.builder().id(6L).build(), "web", "https://velog.io/@skydoves/open-source-machenism")
			));
			tilRepository.saveAll(Arrays.asList(
					newTil(Step.builder().id(1L).build(), "10월 1일의 TIL", "이것은 내용입니다.", false, "이것은 제출할 내용입니다.")
			));
		};
	}

	private User newUser(String email, String name, PasswordEncoder passwordEncoder){
		return User.builder()
				.email(email)
				.name(name)
				.password(passwordEncoder.encode("hongHong!"))
				.role(Role.ROLE_USER)
				.build();
	}

	private Roadmap newIndividualRoadmap(String creator, Category category, String name, Long stepNum){
		return Roadmap.builder()
				.creator(creator)
				.category(category)
				.name(name)
				.stepNum(stepNum)
				.build();
	}

	private Roadmap newGroupRoadmap(String creator, Category category, String name, String description, boolean isPublic, Long currentNum, String code, boolean isRecruit,Long stepNum) {
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
  
	private Til newTil(Step step, String title, String content, boolean isPersonal, String subContent) {
		return Til.builder()
				.step(step)
				.title(title)
				.content(content)
				.isPersonal(isPersonal)
				.submitContent(subContent)
				.build();
	}
}
