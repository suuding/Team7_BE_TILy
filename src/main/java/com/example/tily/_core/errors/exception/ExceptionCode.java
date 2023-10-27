package com.example.tily._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일을 찾을 수 없습니다."),
    USER_EMAIL_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    USER_PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // code 관련 에러
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "유효기간이 만료되었습니다."),
    CODE_WRONG(HttpStatus.BAD_REQUEST, "잘못된 인증코드입니다."),
    CODE_NOT_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "인증코드를 전송하지 못했습니다."),

    // til 관련 에러
    TIL_ROADMAP_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 로드맵에 til을 생성할 권한이 없습니다."),
    TIL_STEP_EXIST(HttpStatus.BAD_REQUEST, "이미 step에 대한 til이 존재합니다."),
    TIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 til을 찾을 수 없습니다"),
    TIL_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "til을 저장할 권한이 없습니다."),
    TIL_CONTENT_NULL(HttpStatus.BAD_REQUEST, "til의 내용을 입력해주세요."),
    TIL_VIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    TIL_SUBMIT_FORBIDDEN(HttpStatus.FORBIDDEN, "til을 제출할 권한이 없습니다."),
    TIL_ALREADY_SUBMIT(HttpStatus.BAD_REQUEST, "이미 til을 제출하였습니다."),
    TIL_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 til을 삭제할 권한이 없습니다."),

    // roadmap 관련 에러
    ROADMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 roadmap을 찾을 수 없습니다."),
    ROADMAP_SUBMIT_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 로드냅에 속하지 않았습니다."),

    // step 관련 에러
    STEP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 step을 찾을 수 없습니다."),

    DATE_WRONG(HttpStatus.BAD_REQUEST, "입력한 날짜를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
