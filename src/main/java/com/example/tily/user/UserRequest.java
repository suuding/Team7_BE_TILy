package com.example.tily.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

public class UserRequest {

    public record JoinDTO(@NotBlank(message = "이메일을 입력해주세요.")
                          @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") String email,
                          @NotBlank(message="이름을 입력해주세요.")
                          @Size(min=2, max=20, message = "이름은 2자에서 20자 이내여야 합니다.") String name,
                          @NotBlank(message = "비밀번호를 입력해주세요.")
                          @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
                          @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") String password) {
        public User toEntity() {
            return User.builder()
                    .email(email)
                    .name(name)
                    .password(password)
                    .role(Role.ROLE_USER)
                    .build();
        }
    }

    public record CheckEmailDTO(@NotBlank(message = "이메일을 입력해주세요.")
                                @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") String email){
    }

    public record LoginDTO(@NotBlank(message = "이메일을 입력해주세요.")
                           @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") String email,
                           @NotBlank(message="비밀번호를 입력해주세요.")
                           @Size(min=8, max=20, message = "8자에서 20자 이내여야 합니다.")
                           @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") String password){
    }

    public record ChangePwdDTO(@NotBlank(message = "이메일을 입력해주세요.")
                               @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") String email,
                               @NotBlank(message="비밀번호를 입력해주세요.")
                               @Size(min=8, max=20, message = "8자에서 20자 이내여야 합니다.")
                               @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") String password){
    }

    public record SendEmailCodeDTO(@NotBlank(message = "이메일을 입력해주세요.")
                                   @Pattern(regexp = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com|[A-Za-z0-9.-]+\\.co.kr|[A-Za-z0-9.-]+$") String email){
    }

    public record CheckEmailCodeDTO(@NotBlank(message="코드를 입력해 주세요.")String code){
    }
}
