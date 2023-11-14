package com.side.data.util

enum class ResponseCodeStatus(val code: Int, val message: String) {

    // User
    LOGIN_SUCCESS      			 (100, "로그인에 성공하였습니다."),
    USER_REQUEST_SUCCESS      			 (101, "요청을 성공적으로 수행했습니다."),
    INVALID_PARAMETER_FAIL      			 (-100, "잘못된 파라미터입니다."),
    USER_NOT_FOUND      			 (-101, "존재하지 않는 유저입니다."),
    SEQ_NOT_FOUND      			 (-102, "해당 SEQ를 가진 유저가 존재하지 않습니다."),
    EMAIL_EXISTS      			 (-103, "이미 존재하는 이메일입니다."),
    UNAUTHORIZED      			 (-104, "인증에 실패했습니다."),
    INTERNAL_SERVER_ERROR      			 (-105, "서버 오류입니다."),
    DELETED_USER      			 (-106, "삭제된 회원입니다."),
    BAD_PASSWORD      			 (-107, "잘못된 패스워드입니다."),
    UNAUTHORIZED_USER      			 (-108, "권한 없는 사용자입니다."),
    REDIRECT_NOT_FOUND      			 (-109, "리디렉션 URI를 포함해야 합니다"),
    NOT_RESOURCE_CREATE_USER             (-110,"리소스를 생성한 유저가 아닙니다."),

    // Record
    CREATE_RECORD_SUCCESS      			 (200, "기록 등록에 성공하였습니다."),
    GET_MY_TOTAL_RECORD_SUCCESS      			 (201, "내 기록 누적 수치 조회에 성공하였습니다."),
    GET_MY_RECORD_SUCCESS      			 (202, "내 기록 수치 조회에 성공하였습니다."),
    GET_ONE_RECORD_SUCCESS      			 (203, "기록 상세 조회에 성공하였습니다."),
    GET_ALL_RECORD_SUCCESS      			 (204, "기록 전체 조회에 성공하였습니다."),
    ADD_COORDINATES_SUCCESS      			 (205, "좌표 등록에 성공하였습니다."),
    ADD_COORDINATES_FAIL      			 (206, "좌표 등록에 실패하였습니다."),
    RECORD_ALREADY_EXIST      			 (-200, "오늘 이미 기록을 등록 하였습니다."),

    // Challenge
    GET_ONE_CHALLENGE_SUCCESS(300, "챌린지 상세 조회에 성공하였습니다."),
    GET_ALL_CHALLENGE_SUCCESS(301, "챌린지 전체 조회에 성공하였습니다."),
    JOIN_CHALLENGE_SUCCESS(302, "챌린지 가입에 성공하였습니다."),
    JOIN_CHALLENGE_FAIL(303, "챌린지 가입에 실패하였습니다."),
    CHECK_IN_CHALLENGE_SUCCESS(304, "챌린지 가입되어 있는 계정입니다."),
    CHECK_IN_CHALLENGE_FAIL(305, "챌린지 가입되지 않은 계정입니다."),
    GET_MY_CHALLENGE_SUCCESS(306, "가입한 챌린지 조회에 성공하였습니다."),
    CHALLENGE_NOT_FOUND(-300, "존재하지 않는 챌린지입니다."),
    CHALLENGE_JOIN_ALREADY_EXIST(-301, "이미 가입한 챌린지 입니다."),
    CHALLENGE_IS_NOT_MY_CHALLENGE(-307, "챌린지에 가입되어 있지 않습니다."),

    // Board
    CREATE_BOARD_SUCCESS      			 (400, "게시글 등록에 성공하였습니다."),
    GET_ONE_BOARD_SUCCESS      			 (401, "게시글 상세 조회에 성공하였습니다."),
    GET_ALL_BOARD_SUCCESS      			 (402, "게시글 전체 조회에 성공하였습니다."),
    DELETE_BOARD_SUCCESS      			 (403, "게시글 삭제에 성공하였습니다."),
    WARN_BOARD_SUCCESS      			 (404, "게시글 신고에 성공하였습니다."),
    WARN_BOARD_FAIL      			 (405, "게시글 신고에 실패하였습니다."),
    BOARD_NOT_FOUND      			 (-400, "존재하지 않는 게시글 입니다."),
    WARN_BOARD_ALREADY_EXIST      			 (-401, "이미 신고한 게시글 입니다."),

    // Challenge 등록
    CREATE_CHALLENGE_FAIL(-306, "챌린지 등록에 실패했습니다."),
    CREATE_CHALLENGE_SUCCESS(307, "챌린지 등록에 성공했습니다."),

    // Challenge 탈퇴
    DELETE_CHALLENGE_SUCCESS(308, "챌린지 해체에 성공하였습니다."),
    LEAVE_CHALLENGE_SUCCESS(309, "가입한 챌린지에 탈퇴하였습니다."),

    // Image
    IMAGE_NOT_FOUND      			 (-500, "이미지를 찾을 수 없습니다."),
    FAILED_CONVERT      			 (-501, "잘못된 파일입니다."),

    // Auth
    FAILED_GENERATE_TOKEN      			 (-600, "토큰을 생성할 수 없습니다."),
    HEADER_NO_TOKEN      			 (-601, "헤더에 토큰이 존재하지 않습니다."),
    INVALID_JWT_SIGNATURE      			 (-602, "시그니처가 유효하지 않습니다."),
    INVALID_JWT_TOKEN      			 (-603, "JWT Token이 유효하지 않습니다."),
    EXPIRED_JWT_TOKEN      			 (-604, "JWT Token이 만료되었습니다."),
    UNSUPPORTED_JWT_TOKEN      			 (-605, "지원되지 않는 Token입니다."),

}