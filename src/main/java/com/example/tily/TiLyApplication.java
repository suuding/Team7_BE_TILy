package com.example.tily;

import com.example.tily.alarm.Alarm;
import com.example.tily.alarm.AlarmRepository;
import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Category;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.step.relation.UserStep;
import com.example.tily.step.relation.UserStepRepository;
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
import java.time.Month;
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
									   UserRoadmapRepository userRoadmapRepository, UserStepRepository userStepRepository, CommentRepository commentRepository, AlarmRepository alarmRepository) {

		return args -> {
			userRepository.saveAll(Arrays.asList(
					newUser("tngus@test.com", "su", passwordEncoder, Role.ROLE_USER),
					newUser("hong@naver.com", "hong", passwordEncoder, Role.ROLE_USER),
					newUser("test@test.com", "test", passwordEncoder, Role.ROLE_USER),
					newUser("admin@test.com", "admin", passwordEncoder, Role.ROLE_ADMIN),
					newUser("hoyai@naver.com", "masterHong", passwordEncoder, Role.ROLE_USER),
					newUser("applier@test.com", "applier", passwordEncoder, Role.ROLE_USER),
					newUser("metameta@test.com", "meta", passwordEncoder, Role.ROLE_USER)
			));
			roadmapRepository.saveAll(Arrays.asList(
					newIndividualRoadmap(User.builder().userId(1L).build(), Category.CATEGORY_INDIVIDUAL, "스프링 시큐리티", 10), //1
					newIndividualRoadmap(User.builder().userId(2L).build(), Category.CATEGORY_INDIVIDUAL, "JPA 입문", 10),
					newIndividualRoadmap(User.builder().userId(3L).build(), Category.CATEGORY_INDIVIDUAL, "자바 reflection", 10),

					newTilyRoadmap(User.builder().userId(4L).build(), Category.CATEGORY_TILY, "spring boot - 초급편", "틸리에서 제공하는 spring boot 초급자를 위한 로드맵입니다.", 20, "image.jpg"), //4
					newTilyRoadmap(User.builder().userId(4L).build(), Category.CATEGORY_TILY, "spring boot - 중급편", "틸리에서 제공하는 spring boot 중급자를 위한 로드맵입니다.", 30, "image.jpg"),
					newTilyRoadmap(User.builder().userId(4L).build(), Category.CATEGORY_TILY, "spring boot - 고급편", "틸리에서 제공하는 spring boot 고급자를 위한 로드맵입니다.", 30, "image.jpg"),
					newTilyRoadmap(User.builder().userId(4L).build(), Category.CATEGORY_TILY, "Spring JPA - 초급편", "틸리에서 제공하는 Spring JPA 초급자를 위한 로드맵입니다.", 20, "image.jpg"),
					newTilyRoadmap(User.builder().userId(4L).build(), Category.CATEGORY_TILY, "Spring JPA - 중급편", "틸리에서 제공하는 Spring JPA 중급자를 위한 로드맵입니다.", 20, "image.jpg"),
					newTilyRoadmap(User.builder().userId(4L).build(), Category.CATEGORY_TILY, "Spring JPA - 고급편", "틸리에서 제공하는 Spring JPA 고급자를 위한 로드맵입니다.", 20, "image.jpg"),

					newGroupRoadmap(User.builder().userId(1L).build(), Category.CATEGORY_GROUP, "JAVA 입문 수업 - 생활 코딩", "생활 코딩님의 로드맵입니다!", true, "pnu12345", true, 10), // 10
					newGroupRoadmap(User.builder().userId(1L).build(), Category.CATEGORY_GROUP, "spring boot 스터디", "같이 spring boot 공부해봐요!", true, "pnu54321", true, 10),
					newGroupRoadmap(User.builder().userId(2L).build(), Category.CATEGORY_GROUP, "JPA 스터디", "김영한 강사님의 JPA를 공부하는 스터디 ^^", false, "hoyai123", false, 20),
					newGroupRoadmap(User.builder().userId(2L).build(), Category.CATEGORY_GROUP, "데이터베이스 스터디", "데이터베이스 공부합시다", true, "hoyoung1", true, 15),
					newGroupRoadmap(User.builder().userId(3L).build(), Category.CATEGORY_GROUP, "카카오테크캠퍼스 1단계", "카카오테크캠퍼스 1단계입니다.", false, "kakao123", true, 20)
			));

			userRoadmapRepository.saveAll(Arrays.asList(
					newUserRoadmapRelation(Roadmap.builder().roadmapId(1L).build(), User.builder().userId(1L).build(), null, true, GroupRole.ROLE_MASTER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(2L).build(), User.builder().userId(2L).build(), null, true, GroupRole.ROLE_MASTER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(3L).build(), User.builder().userId(3L).build(), null, true, GroupRole.ROLE_MASTER, 0),

					newUserRoadmapRelation(Roadmap.builder().roadmapId(4L).build(), User.builder().userId(4L).build(), null, true, GroupRole.ROLE_MASTER, 10),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(4L).build(), User.builder().userId(1L).build(), null, true, GroupRole.ROLE_MEMBER, 10),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(4L).build(), User.builder().userId(2L).build(), null, true, GroupRole.ROLE_MEMBER, 20),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(4L).build(), User.builder().userId(3L).build(), null, true, GroupRole.ROLE_MEMBER, 100),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(5L).build(), User.builder().userId(4L).build(), null, true, GroupRole.ROLE_MASTER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(6L).build(), User.builder().userId(4L).build(), null, true, GroupRole.ROLE_MASTER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(7L).build(), User.builder().userId(4L).build(), null, true, GroupRole.ROLE_MASTER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(7L).build(), User.builder().userId(1L).build(), null, true, GroupRole.ROLE_MEMBER, 100),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(7L).build(), User.builder().userId(2L).build(), null, true, GroupRole.ROLE_MEMBER, 20),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(7L).build(), User.builder().userId(3L).build(), null, true, GroupRole.ROLE_MEMBER, 100),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(8L).build(), User.builder().userId(4L).build(), null, true, GroupRole.ROLE_MASTER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(9L).build(), User.builder().userId(4L).build(), null, true, GroupRole.ROLE_MASTER, 0),

					newUserRoadmapRelation(Roadmap.builder().roadmapId(10L).build(), User.builder().userId(2L).build(), "자바 공부하고싶습니다!", true, GroupRole.ROLE_MANAGER, 10),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(10L).build(), User.builder().userId(3L).build(), "자바 공부하고싶습니다!", true, GroupRole.ROLE_MEMBER, 10),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(12L).build(), User.builder().userId(1L).build(), "열심히 하겠습니다!", true, GroupRole.ROLE_MEMBER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(12L).build(), User.builder().userId(2L).build(), "열심히 하겠습니다2!", true, GroupRole.ROLE_MEMBER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(13L).build(), User.builder().userId(1L).build(), "열심히 하겠습니다!", false, GroupRole.ROLE_NONE, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(12L).build(), User.builder().userId(5L).build(), "매니저", true, GroupRole.ROLE_MANAGER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(10L).build(), User.builder().userId(6L).build(), "참가 신청합니다", false, GroupRole.ROLE_MEMBER, 0),
					newUserRoadmapRelation(Roadmap.builder().roadmapId(10L).build(), User.builder().userId(7L).build(), "참가 신청합니다", false, GroupRole.ROLE_MEMBER, 0)
			));

			stepRepository.saveAll(Arrays.asList(
					newIndividualStep(Roadmap.builder().roadmapId(1L).build(), "스프링 시큐리티를 사용하는 이유"),
					newIndividualStep(Roadmap.builder().roadmapId(1L).build(), "OAuth 2.0으로 로그인 기능 구현하기"),
					newIndividualStep(Roadmap.builder().roadmapId(1L).build(), "인증된 사용자 권한 부족 예외처리"),
					newIndividualStep(Roadmap.builder().roadmapId(1L).build(), "소셜 로그인 사용하기"),

					newGroupStep(Roadmap.builder().roadmapId(12L).build(), "다형성(Polymorphism)", "Day1", LocalDateTime.of(2023, 10, 1, 23, 59)),
					newGroupStep(Roadmap.builder().roadmapId(12L).build(), "람다식(lambda expression)", "Day2", LocalDateTime.of(2023, 12, 3, 23, 59)),
					newGroupStep(Roadmap.builder().roadmapId(12L).build(), "스트림(lambda expression)", "Day3", LocalDateTime.of(2023, 10, 5, 23, 59)),


					newGroupStep(Roadmap.builder().roadmapId(5L).build(), "spring boot 1일차", "Day1", LocalDateTime.of(2023, 10, 1, 23, 59)),
					newGroupStep(Roadmap.builder().roadmapId(5L).build(), "spring boot 2일차", "Day2", LocalDateTime.of(2023, 10, 3, 23, 59)),
					newGroupStep(Roadmap.builder().roadmapId(5L).build(), "spring boot 3일차", "Day3", LocalDateTime.of(2023, 10, 5, 23, 59)),
					newIndividualStep(Roadmap.builder().roadmapId(1L).build(), "예외처리 마스터")
			));
			userStepRepository.saveAll(Arrays.asList(
					newUserStepRelation(Step.builder().stepId(5L).build(), User.builder().userId(1L).build(), true),
					newUserStepRelation(Step.builder().stepId(5L).build(), User.builder().userId(2L).build(), true),
					newUserStepRelation(Step.builder().stepId(6L).build(), User.builder().userId(1L).build(), false),
					newUserStepRelation(Step.builder().stepId(6L).build(), User.builder().userId(2L).build(), false),
					newUserStepRelation(Step.builder().stepId(6L).build(), User.builder().userId(5L).build(), false)
//					newUserStepRelation(Step.builder().id(5L).build(), User.builder().id(1L).build(), false),
//					newUserStepRelation(Step.builder().id(6L).build(), User.builder().id(1L).build(), false),
//					newUserStepRelation(Step.builder().id(7L).build(), User.builder().id(1L).build(), false)
			));
			referenceRepository.saveAll(Arrays.asList(
					newReference(Step.builder().stepId(4L).build(), "youtube", "https://www.youtube.com/watch?v=0L6QWKC1a6k"),
					newReference(Step.builder().stepId(4L).build(), "youtube", "https://www.youtube.com/watch?v=U8LVCTaS3mQ"),
					newReference(Step.builder().stepId(6L).build(), "youtube", "https://www.youtube.com/watch?v=1OLy4Dj_zCg"),
					newReference(Step.builder().stepId(4L).build(), "web", "https://blog.naver.com/hoyai-/1234"),
					newReference(Step.builder().stepId(5L).build(), "web", "https://blog.naver.com/cestlavie_01/1234"),
					newReference(Step.builder().stepId(5L).build(), "web", "https://velog.io/@skydoves/open-source-machenism")
			));
			tilRepository.saveAll(Arrays.asList(
					newIndividualTil(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(1L).build(), User.builder().userId(1L).build(), "스프링 시큐리티를 사용하는 이유", "이것은 내용입니다.", true, null),
					newIndividualTil(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(2L).build(), User.builder().userId(1L).build(), "OAuth 2.0으로 로그인 기능 구현하기", "이것은 내용입니다.", true, null),
					newIndividualTil(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(3L).build(), User.builder().userId(1L).build(), "인증된 사용자 권한 부족 예외처리", "이것은 내용입니다.", true, null),
					newIndividualTil(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(4L).build(), User.builder().userId(1L).build(), "소셜 로그인 사용하기", "이것은 내용입니다.", true, null),

					newGroupTil(Roadmap.builder().roadmapId(12L).build(), Step.builder().stepId(5L).build(), User.builder().userId(1L).build(), "다형성(Polymorphism)", "이것은 내용입니다1.", "이것은 제출할 내용입니다.", LocalDateTime.of(2023, Month.OCTOBER, 10, 1, 0, 0), 1, false),
					newGroupTil(Roadmap.builder().roadmapId(12L).build(), Step.builder().stepId(5L).build(), User.builder().userId(2L).build(), "람다식(lambda expression)", "이것은 내용입니다2.", "이것은 제출할 내용입니다.", LocalDateTime.of(2023, Month.OCTOBER, 10, 2, 0, 0), 2, false),
					newGroupTil(Roadmap.builder().roadmapId(12L).build(), Step.builder().stepId(6L).build(), User.builder().userId(1L).build(), "스트림(lambda expression)", "이것은 내용입니다3.", "이것은 제출할 내용입니다.", null, 3, false),
					newGroupTil(Roadmap.builder().roadmapId(12L).build(), Step.builder().stepId(6L).build(), User.builder().userId(2L).build(), "다형성2(Polymorphism)", "이것은 내용입니다4.", "이것은 제출할 내용입니다.", null, 4, false),
					newGroupTil(Roadmap.builder().roadmapId(12L).build(), Step.builder().stepId(6L).build(), User.builder().userId(5L).build(), "TIL!!", "이것은 내용입니다5.", null, null, 0, false)
			));

			commentRepository.saveAll(Arrays.asList(
					newComment(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(1L).build(), Til.builder().tilId(1L).build(), User.builder().userId(1L).build(), "이것은 댓글입니다."),
					newComment(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(1L).build(), Til.builder().tilId(1L).build(), User.builder().userId(2L).build(), "이것도 댓글입니다."),
					newComment(Roadmap.builder().roadmapId(1L).build(), Step.builder().stepId(2L).build(), Til.builder().tilId(2L).build(), User.builder().userId(1L).build(), "이것도 댓글입니다.")
			));

			alarmRepository.saveAll(Arrays.asList(
					newAlarm(Til.builder().tilId(5L).build(), User.builder().userId(1L).build(), false)
			));
		};
	}

	private User newUser(String email, String name, PasswordEncoder passwordEncoder, Role role) {
		return User.builder()
				.email(email)
				.name(name)
				.password(passwordEncoder.encode("hongHong1!"))
				.role(role)
				.build();
	}

	private Roadmap newIndividualRoadmap(User creator, Category category, String name, int stepNum) {
		return Roadmap.builder()
				.creator(creator)
				.category(category)
				.name(name)
				.stepNum(stepNum)
				.isPublic(false)
				.isRecruit(false)
				.build();
	}

	public Roadmap newTilyRoadmap(User creator, Category category, String name, String description, int stepNum, String image) {
		return Roadmap.builder()
				.creator(creator)
				.category(category)
				.name(name)
				.description(description)
				.stepNum(stepNum)
				.image(image)
				.isPublic(true)
				.isRecruit(true)
				.build();
	}

	private Roadmap newGroupRoadmap(User creator, Category category, String name, String description, boolean isPublic, String code, boolean isRecruit, int stepNum) {
		return Roadmap.builder()
				.creator(creator)
				.category(category)
				.name(name)
				.description(description)
				.isPublic(isPublic)
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

	private Step newIndividualStep(Roadmap roadmap, String title) {
		return Step.builder()
				.roadmap(roadmap)
				.title(title)
				.build();
	}

	private Step newGroupStep(Roadmap roadmap, String title, String description, LocalDateTime dueDate) {
		return Step.builder()
				.roadmap(roadmap)
				.title(title)
				.description(description)
				.dueDate(dueDate)
				.build();
	}

	private UserStep newUserStepRelation(Step step, User user, Boolean isSubmit) {
		return UserStep.builder()
				.step(step)
				.user(user)
				.isSubmit(isSubmit)
				.build();
	}

	private Reference newReference(Step step, String category, String link) {
		return Reference.builder()
				.step(step)
				.category(category)
				.link(link)
				.build();
	}

	private Til newIndividualTil(Roadmap roadmap, Step step, User writer, String title, String content, boolean isPersonal, String subContent) {
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

	private Comment newComment(Roadmap roadmap, Step step, Til til, User writer, String content) {
		return Comment.builder()
				.til(til)
				.writer(writer)
				.content(content)
				.build();
	}


	private Til newGroupTil(Roadmap roadmap, Step step, User writer, String title, String content, String subContent, LocalDateTime submitDate, int commentNum, boolean isPersonal) {
		return Til.builder()
				.roadmap(roadmap)
				.step(step)
				.writer(writer)
				.title(title)
				.content(content)
				.submitContent(subContent)
				.submitDate(submitDate)
				.commentNum(commentNum)
				.isPersonal(isPersonal)
				.build();
	}

	private Alarm newAlarm(Til til, User receiver, Boolean isRead) {
		return Alarm.builder()
				.til(til)
				.receiver(receiver)
				.isRead(false)
				.build();
	}
}
