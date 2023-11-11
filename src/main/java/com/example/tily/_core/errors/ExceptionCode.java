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
    USER_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "사용자 정보를 수정할 권한이 없습니다."),
    USER_CURPASSWORD_WRONG(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다"),

    // code 관련 에러
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "유효기간이 만료되었습니다."),
    CODE_WRONG(HttpStatus.BAD_REQUEST, "잘못된 인증코드입니다."),
    CODE_NOT_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "인증코드를 전송하지 못했습니다."),

    // til 관련 에러
    TIL_ROADMAP_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 로드맵에 til을 생성할 권한이 없습니다."),
    TIL_STEP_EXIST(HttpStatus.BAD_REQUEST, "해당 step에 대한 til이 이미 존재합니다."),
    TIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 til을 찾을 수 없습니다"),
    TIL_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "til을 저장할 권한이 없습니다."),
    TIL_CONTENT_NULL(HttpStatus.BAD_REQUEST, "til의 내용을 입력해주세요."),
    TIL_VIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    TIL_SUBMIT_FORBIDDEN(HttpStatus.FORBIDDEN, "til을 제출할 권한이 없습니다."),
    TIL_ALREADY_SUBMIT(HttpStatus.BAD_REQUEST, "이미 til을 제출하였습니다."),
    TIL_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 til을 삭제할 권한이 없습니다."),
    TIL_END_DUEDATE(HttpStatus.BAD_REQUEST, "제출 시간이 지나 til을 제출할 수 없습니다."),
    TIL_FORBIDDEN(HttpStatus.FORBIDDEN, "til에 대한 권한이 없습니다."),

    // roadmap 관련 에러
    ROADMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 roadmap을 찾을 수 없습니다."),
    ROADMAP_NOT_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 로드맵에 속하지 않았습니다."),
    ROADMAP_NOT_BELONG(HttpStatus.FORBIDDEN, "해당 roadmap에 속하지 않습니다."),
    ROADMAP_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 roadmap에 접근할 권한이 없습니다."),
    ROADMAP_SUBMIT_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 til을 제출할 권한이 없습니다."),
    ROADMAP_ALREADY_APPLY(HttpStatus.BAD_REQUEST, "해당 로드맵에 이미 신청했습니다."),
    ROADMAP_REJECT(HttpStatus.BAD_REQUEST, "로드맵 신청이 거절되었습니다."),
    ROADMAP_ALREADY_MEMBER(HttpStatus.BAD_REQUEST, "이미 해당 로드맵에 속했습니다."),
    ROADMAP_DISMISS_FORBIDDEN(HttpStatus.FORBIDDEN, "로드맵의 master를 강퇴할 권한이 없습니다."),
    ROADMAP_ONLY_MASTER(HttpStatus.FORBIDDEN, "master 권한이 필요합니다."),
    ROADMAP_END_RECRUIT(HttpStatus.BAD_REQUEST, "해당 로드맵은 모집을 종료했습니다."),

    // step 관련 에러
    STEP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 step을 찾을 수 없습니다."),
    STEP_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 step을 조회할 권한이 없습니다."),
    STEP_NOT_BELONG(HttpStatus.BAD_REQUEST, "해당 step은 roadmap에 속하지 않았습니다."),
    STEP_ROADMAP_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 로드맵에 step을 생성할 권한이 없습니다."),

    // comment 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 댓글을 수정할 권한이 없습니다."),
    COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 댓글을 삭제할 권한이 없습니다."),

    // alarm 관련 에러
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),

    DATE_WRONG(HttpStatus.BAD_REQUEST, "잘못된 날짜 형식입니다."),

    // reference 관련 에러
    REFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 reference를 찾을 수 없습니다."),
    REFERENCE_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 reference를 삭제할 권한이 없습니다"),

    // image 관련 에러
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 image를 찰을 수 없습니다."),
    IMAGE_DOWNLOAD_FAIL(HttpStatus.NOT_FOUND, "파일이 서버에 존재하지 않거나 다운로드 경로가 잘못되었습니다"),

    // file 관련 에러
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 에러가 발생하였습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바른 형식을 입력해주세요."),

    // token 관련
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료됐습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
