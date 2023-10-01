package com.example.tily.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

public class UserRequest {

    @Getter @Setter
    public static class JoinDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;

        @NotBlank(message="이름을 입력해주세요.")
        @Size(min=2, max=20, message = "이름은 2자에서 20자 이내여야 합니다.")
        private String name;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
        private String password;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .name(name)
                    .password(password)
                    .role(Role.ROLE_USER)
                    .build();
        }
    }

    @Getter @Setter
    public static class CheckEmailDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;
    }

    @Getter @Setter
    public static class LoginDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;

        @NotBlank(message="비밀번호를 입력해주세요.")
        @Size(min=8, max=20, message = "8자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
        private String password;
    }

    @Getter @Setter
    public static class ChangePwdDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;

        @NotBlank(message="비밀번호를 입력해주세요.")
        @Size(min=8, max=20, message = "8자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
        private String password;
    }

    @Getter @Setter
    public static class SendEmailCodeDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;
    }

    @Getter @Setter
    public static class CheckEmailCodeDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;

        @NotBlank(message = "코드를 입력해주세요.")
        private String code;
    }
}
