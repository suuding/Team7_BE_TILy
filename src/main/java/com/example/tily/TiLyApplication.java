package com.example.tily;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
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

import java.util.Arrays;

@EnableJpaAuditing
@SpringBootApplication
public class TiLyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiLyApplication.class, args);
	}

	@Profile("local")
	@Bean
	CommandLineRunner localServerStart(UserRepository userRepository, RoadmapRepository roadmapRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			userRepository.saveAll(Arrays.asList(newUser("hong@naver.com", "hong", passwordEncoder)));
			roadmapRepository.saveAll(Arrays.asList(
					newIndividualRoadmap("hong","individual", "스프링 시큐리티", 10L),
					newIndividualRoadmap("puuding","individual", "JPA 입문", 10L),
					newIndividualRoadmap("sam-mae","individual", "자바 reflection", 10L)
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

	private Roadmap newIndividualRoadmap(String creator, String category, String name, Long stepNum){
		return Roadmap.builder()
				.creator(creator)
				.category(category)
				.name(name)
				.stepNum(stepNum)
				.build();
	}
}
