package com.example.tily.user;

import javax.validation.constraints.*;

public class UserRequest {

    public record JoinDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") 
            String email,
            @NotBlank(message="이름을 입력해주세요.")
            @Size(min=2, max=20, message = "이름은 2자에서 20자 이내여야 합니다.") 
            String name,
            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") 
            String password,
            @NotBlank(message = "확인 비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "비밀번호는 8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") 
            String passwordConfirm
    ) { }

    public record CheckEmailDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") 
            String email
    ) { }

    public record LoginDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") 
            String email,
            @NotBlank(message="비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") 
            String password
    ) { }

    public record ChangePwdDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.") 
            String email,
            @NotBlank(message="비밀번호를 입력해주세요.")
            @Size(min=8, max=20, message = "8자에서 20자 이내여야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "올바른 비밀번호 형식을 입력해주세요.") 
            String password
    ) { }

    public record SendEmailCodeDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
            String email
    ) { }

    public record CheckEmailCodeDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식을 입력해주세요.")
            String email,
            @NotBlank(message = "코드를 입력해주세요.")
            String code
    ) { }

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
            String newPasswordConfirm
    ) { }
}
