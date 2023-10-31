package com.example.tily.user;

import javax.validation.constraints.*;

public class UserRequest {

<<<<<<< HEAD
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
=======
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
>>>>>>> upstream/weekly
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

    public record SendEmailCodeDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
            String email) {}

    public record CheckEmailCodeDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
            String email,
            @NotBlank(message = "코드를 입력해주세요.")
            String code) {}

<<<<<<< HEAD
    public record UpdateUserDTO(
            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
            String curPassword,
            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
            String newPassword,
            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
            String newPasswordConfirm) {}
=======
    @Getter @Setter
    public static class CheckEmailCodeDTO {

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
        private String email;

        @NotBlank(message = "코드를 입력해주세요.")
        private String code;
    }

    @Getter @Setter
    public static class UpdateUserDTO {
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
        private String curPassword;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
        private String newPassword;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.")
        private String newPasswordConfirm;
    }
>>>>>>> upstream/weekly
}
