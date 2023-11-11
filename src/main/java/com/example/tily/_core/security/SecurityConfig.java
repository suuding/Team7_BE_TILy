package com.example.tily._core.security;


import com.example.tily._core.errors.CustomException;
import com.example.tily._core.errors.ExceptionCode;
import com.example.tily._core.utils.FilterResponseUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 해제
        http.csrf().disable();

        // iframe 거부
        http.headers().frameOptions().sameOrigin();

        // cors 재설정
        http.cors().configurationSource(configurationSource());

        // jSessionId 사용 거부 (5번을 설정하면 jsessionId가 거부되기 때문에 4번은 사실 필요 없다)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // form 로그인 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        http.formLogin().disable();

        // 로그인 인증창이 뜨지 않게 비활성화
        http.httpBasic().disable();

        // 커스텀 필터 적용 (시큐리티 필터 교환)
        http.apply(new CustomSecurityFilterManager());

        // 인증 실패 처리
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            FilterResponseUtils.unAuthorized(response, new CustomException(ExceptionCode.USER_UNAUTHORIZED));
        });

        // 권한 실패 처리
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            FilterResponseUtils.forbidden(response, new CustomException(ExceptionCode.USER_FORBIDDEN));
        });

        // 인증, 권한 필터 설정
        http.authorizeRequests(
                authorize -> authorize.antMatchers("/api/email/**", "/api/join", "/api/login", "/api/password/**", "/api/auth/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/roadmaps").permitAll()
                        .antMatchers("/api/roadmaps/my").authenticated()
                        .antMatchers(HttpMethod.GET, "/api/roadmaps/{roadmapId}").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/refresh").permitAll()
                        .antMatchers("/api/roadmaps/**", "/api/alarms/**", "/api/comments/**", "/api/images/**", "/api/references/**", "/api/references/**", "/api/steps/**", "/api/tils/**", "/api/users/**", "/api/gardens").authenticated()
                        .anyRequest().permitAll()
        );

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 앤드 IP만 허용 react)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 옛날에는 디폴트 였다. 지금은 아닙니다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
