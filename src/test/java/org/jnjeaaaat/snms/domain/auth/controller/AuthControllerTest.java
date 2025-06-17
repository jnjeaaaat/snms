package org.jnjeaaaat.snms.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.*;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.VerifyCodeResponse;
import org.jnjeaaaat.snms.domain.auth.exception.AuthException;
import org.jnjeaaaat.snms.domain.auth.service.AuthService;
import org.jnjeaaaat.snms.domain.auth.service.CoolSmsService;
import org.jnjeaaaat.snms.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "1", roles = "USER")
@WebMvcTest(
        controllers = AuthController.class
)
@AutoConfigureRestDocs
@ActiveProfiles("test")
class AuthControllerTest {

    @MockBean
    AuthService authService;

    @MockBean
    CoolSmsService coolSmsService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    OperationResponsePreprocessor commonResponsePreprocessor;
    ResponseFieldsSnippet errorResponseSnippet;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    @BeforeEach
    void setUp() {

        params.add("providerName", "google");
        params.add("providerUserId", "12345");
        params.add("email", "test@gmail.com");

        commonResponsePreprocessor = preprocessResponse(
                modifyHeaders()
                        .remove("X-Content-Type-Options")
                        .remove("X-XSS-Protection")
                        .remove("Cache-Control")
                        .remove("Pragma")
                        .remove("Expires")
                        .remove("X-Frame-Options")
                , prettyPrint()
        );

        errorResponseSnippet = responseFields(
                fieldWithPath("errorCode").description("에러 상태"),
                fieldWithPath("message").description("에러 메세지")
        );
    }

    @Nested
    @DisplayName("중복 아이디 확인")
    class CheckUidTest {

        UidCheckRequest request = new UidCheckRequest("testId");
        String requestBody = objectMapper.writeValueAsString(request);

        CheckUidTest() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 중복 아이디 확인")
        void success_check_uid() throws Exception {
            //given
            //when
            doNothing().when(authService).checkUid(request);
            //then
            mockMvc.perform(post("/api/auth/check-uid")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("auth/check-uid/success",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            requestFields(
                                    fieldWithPath("uid")
                                            .description("가입 아이디")
                                            .attributes(
                                                    key("constraints").value("4 - 10자, 알파벳 대소문자, 숫자 입력 가능"),
                                                    key("minLength").value("4"),
                                                    key("maxLength").value("10"),
                                                    key("format").value("영문 대소문자, 숫자"),
                                                    key("pattern").value("^[a-zA-Z0-9]+$"),
                                                    key("validation").value("@NotBlank @ValidUid"),
                                                    key("customValidation").value("@ValidUid: null, pattern, 문자열 길이(4-10) 체크")
                                            )
                            )
                    ));

            verify(authService, times(1)).checkUid(any(UidCheckRequest.class));
        }

        @Test
        @DisplayName("[실패] 중복 아이디 존재")
        void check_uid_DuplicateUid() throws Exception {
            //given
            //when
            doThrow(new AuthException(DUPLICATE_UID)).when(authService).checkUid(request);
            //then
            mockMvc.perform(post("/api/auth/check-uid")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("auth/sign-up/duplicated_uid",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

    }

    @Nested
    @DisplayName("로컬 회원 가입")
    class SignUpTest {

        SignUpRequest request = new SignUpRequest("testId",
                "qwER12!@", "qwER12!@", "test1", "01012341234", "/default.jpg");
        String requestBody = objectMapper.writeValueAsString(request);
        SignUpResponse response = new SignUpResponse(
                1L,
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJzbm1zIiwiaWF0IjoxNzQ4MjY5NDE4LCJleHAiOjE3NDgyNzEyMTh9.9h1Cuq3yNV2yHAzU3K-8glhVjeJpaYKF1xbNTC0oX2dsj5Lmm-ihWHhnBIaiktpkArg4nzsCZXXjj83NEGsmyQ"
        );

        SignUpTest() throws JsonProcessingException {
        }


        @Test
        @DisplayName("[성공] 로컬 회원 가입")
        void success_sign_up() throws Exception {
            //given
            //when
            doReturn(response).when(authService).signUp(
                    request,
                    "google", "12345", "test@gmail.com"
            );
            //then
            mockMvc.perform(post("/api/auth/sign-up")
                            .params(params)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andDo(document("auth/sign-up/success",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            responseHeaders(
                                    headerWithName("Location").description("회원 정보 조회 URI")
                            ),
                            responseCookies(
                                    cookieWithName("access_token").description("JWT 엑세스 토큰")
                            ),
                            queryParameters(
                                    parameterWithName("providerName")
                                            .description("소셜 로그인 제공자 이름")
                                            .optional()
                                            .attributes(
                                                    key("format").value("google, kakao, naver 지원"),
                                                    key("validation").value("@ValidProviderName"),
                                                    key("customValidation").value("@ValidProviderName: null 이거나 google, kakao, naver 일때 통과"),
                                                    key("conditionalRequired").value("소셜 로그인 진행 시 핸드폰 번호에 따른 계정이 없을 때 필수")
                                            ),
                                    parameterWithName("providerUserId")
                                            .description("소셜 로그인 식별자")
                                            .optional()
                                            .attributes(
                                                    key("validation").value("@Nullable @Email"),
                                                    key("conditionalRequired").value("소셜 로그인 진행 시 핸드폰 번호에 따른 계정이 없을 때 필수")
                                            ),
                                    parameterWithName("email")
                                            .description("소셜 로그인 이메일")
                                            .optional()
                                            .attributes(
                                                    key("format").value("email 형식"),
                                                    key("validation").value("@Nullable @Email"),
                                                    key("conditionalRequired").value("소셜 로그인 진행 시 핸드폰 번호에 따른 계정이 없을 때 필수")
                                            )
                            ).and(parameterWithName("_csrf").ignored()),
                            requestFields(
                                    fieldWithPath("uid")
                                            .description("가입 아이디")
                                            .attributes(
                                                    key("constraints").value("4-10자, 알파벳 대소문자, 숫자 입력 가능"),
                                                    key("minLength").value("4"),
                                                    key("maxLength").value("10"),
                                                    key("format").value("영문 대소문자, 숫자"),
                                                    key("pattern").value("^[a-zA-Z0-9]+$"),
                                                    key("validation").value("@NotBlank @ValidUid"),
                                                    key("customValidation").value("@ValidUid: null, pattern, 문자열 길이(4-10) 체크")
                                            ),
                                    fieldWithPath("password")
                                            .description("비밀번호")
                                            .attributes(
                                                    key("constraints").value("8-20자, 영문 대소문자, 숫자, 특수문자 조합"),
                                                    key("minLength").value("8"),
                                                    key("maxLength").value("20"),
                                                    key("format").value("영문 대소문자, 숫자, 특수문자(!@#$%^&*) 포함"),
                                                    key("pattern").value("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$"),
                                                    key("validation").value("@NotBlank @ValidPassword"),
                                                    key("customValidation").value("@ValidPassword: 비밀번호 패턴 체크")
                                            ),
                                    fieldWithPath("confirmPassword")
                                            .description("비밀번호 재확인")
                                            .attributes(
                                                    key("constraints").value("8-20자, 영문 대소문자, 숫자, 특수문자 조합"),
                                                    key("minLength").value("8"),
                                                    key("maxLength").value("20"),
                                                    key("format").value("영문 대소문자, 숫자, 특수문자(!@#$%^&*) 포함"),
                                                    key("pattern").value("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$"),
                                                    key("validation").value("@NotBlank @ValidPassword"),
                                                    key("customValidation").value("@ValidPassword: 비밀번호 패턴 체크")
                                            ),
                                    fieldWithPath("nickname")
                                            .description("닉네임")
                                            .attributes(
                                                    key("constraints").value("1-10자, 영문 대소문자, 숫자, 한글 조합"),
                                                    key("minLength").value("1"),
                                                    key("maxLength").value("10"),
                                                    key("format").value("영문 대소문자, 숫자, 한글 포함"),
                                                    key("pattern").value("^[a-zA-Z0-9가-힣]{1,10}$"),
                                                    key("validation").value("@NotBlank @ValidNickname"),
                                                    key("customValidation").value("@ValidNickname: 닉네임 패턴, 길이(1-10) 체크")

                                            ),
                                    fieldWithPath("phoneNum")
                                            .description("핸드폰 번호")
                                            .attributes(
                                                    key("constraints").value("010,011,016,017,018,019으로 시작하는 7, 8 자리 숫자. '-'이 있을경우 제거하고 DB 저장"),
                                                    key("format").value("010,011,016,017,018,019으로 시작하는 7, 8 자리 숫자"),
                                                    key("pattern").value("^(010|011|016|017|018|019)\\d{7,8}$"),
                                                    key("validation").value("@NotBlank @ValidPhoneNumber"),
                                                    key("customValidation").value("@ValidPhoneNumber: '-'있을 경우 제거하고 패턴 체크")
                                            ),
                                    fieldWithPath("profileImgUrl")
                                            .description("기본 프로필 이미지")
                                            .attributes(
                                                    key("constraints").value("초기 회원가입 시 지정안하고 default 이미지 사용"),
                                                    key("validation").value("@NotBlank @ValidDefaultImg"),
                                                    key("customValidation").value("@ValidDefaultImg: /default.jpg 인지 체크"),
                                                    key("default").value("https://mysnmsbucket.s3.ap-northeast-2.amazonaws.com/default.jpg")
                                            )
                            ),
                            responseFields(
                                    fieldWithPath("id").description("사용자 고유 ID"),
                                    fieldWithPath("accessToken").description("JWT 엑세스 토큰")
                            )
                    ));

            verify(authService, times(1)).signUp(any(SignUpRequest.class), nullable(String.class), nullable(String.class), nullable(String.class));
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 이미 가입한 사용자")
        void sign_up_DuplicatedMember() throws Exception {
            //given
            //when
            doThrow(new AuthException(DUPLICATE_MEMBER)).when(authService).signUp(request, null, null, null);
            //then
            mockMvc.perform(post("/api/auth/sign-up")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("auth/sign-up/duplicated_member",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));

        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 비밀번호 불일치")
        void sign_up_UnmatchedPassword() throws Exception {
            //given
            //when
            doThrow(new AuthException(UNMATCHED_PASSWORD)).when(authService).signUp(request, null, null, null);
            //then
            mockMvc.perform(post("/api/auth/sign-up")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("auth/sign-up/unmatched_password",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

    }

    @Nested
    @DisplayName("로컬 로그인")
    class SignInTest {

        SignInRequest request = new SignInRequest("testId", "qwER12!@");
        String requestBody = objectMapper.writeValueAsString(request);
        SignInResponse signInResponse =
                new SignInResponse(1L, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJzbm1zIiwiaWF0IjoxNzQ4MjY5NDE4LCJleHAiOjE3NDgyNzEyMTh9.9h1Cuq3yNV2yHAzU3K-8glhVjeJpaYKF1xbNTC0oX2dsj5Lmm-ihWHhnBIaiktpkArg4nzsCZXXjj83NEGsmyQ");

        SignInTest() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 로컬 로그인")
        void success_sign_in() throws Exception {
            //given
            //when
            doReturn(signInResponse).when(authService).signIn(request);
            //then
            mockMvc.perform(post("/api/auth/sign-in")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("auth/sign-in/success",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            requestFields(
                                    fieldWithPath("uid")
                                            .description("가입 아이디")
                                            .attributes(
                                                    key("constraints").value("4-10자, 알파벳 대소문자, 숫자 입력 가능"),
                                                    key("minLength").value("4"),
                                                    key("maxLength").value("10"),
                                                    key("format").value("영문 대소문자, 숫자"),
                                                    key("pattern").value("^[a-zA-Z0-9]+$"),
                                                    key("validation").value("@NotBlank @ValidUid"),
                                                    key("customValidation").value("@ValidUid: null, pattern, 문자열 길이(4-10) 체크")
                                            ),
                                    fieldWithPath("password")
                                            .description("비밀번호")
                                            .attributes(
                                                    key("constraints").value("8-20자, 영문 대소문자, 숫자, 특수문자 조합"),
                                                    key("minLength").value("8"),
                                                    key("maxLength").value("20"),
                                                    key("format").value("영문 대소문자, 숫자, 특수문자(!@#$%^&*) 포함"),
                                                    key("pattern").value("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$"),
                                                    key("validation").value("@NotBlank @ValidPassword"),
                                                    key("customValidation").value("@ValidPassword: 비밀번호 패턴 체크")
                                            )
                            ),
                            responseFields(
                                    fieldWithPath("id").description("사용자 고유 ID"),
                                    fieldWithPath("accessToken").description("JWT 엑세스 토큰")
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 로컬 로그인 - 회원 없음")
        void sign_in_NotFoundMember() throws Exception {
            //given
            //when
            doThrow(new AuthException(NOT_FOUND_MEMBER)).when(authService).signIn(request);
            //then
            mockMvc.perform(post("/api/auth/sign-in")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andDo(document("auth/sign-in/not_found_member",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

        @Test
        @DisplayName("[실패] 로컬 로그인 - 비밀번호 인증 실패")
        void sign_in_WrongPassword() throws Exception {
            //given
            //when
            doThrow(new AuthException(WRONG_PASSWORD)).when(authService).signIn(request);
            //then
            mockMvc.perform(post("/api/auth/sign-in")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("auth/sign-in/wrong_password",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

    }

    @Test
    @DisplayName("[성공] 로그아웃")
    void success_sign_out() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/api/auth/sign-out")
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/sign-out/success",
                        preprocessRequest(prettyPrint()),
                        commonResponsePreprocessor
                ));

        verify(authService).signOut(any(UserDetails.class));
    }

    @Test
    @DisplayName("[성공] 인증번호 전송")
    void success_send_sms() throws Exception {
        //given
        SmsSendRequest request = new SmsSendRequest("01012341234");
        String requestBody = objectMapper.writeValueAsString(request);

        //when
        doNothing().when(coolSmsService).sendSms(any(HttpServletRequest.class), eq(request));
        //then
        mockMvc.perform(post("/api/auth/send-sms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/send-sms/success",
                        preprocessRequest(prettyPrint()),
                        commonResponsePreprocessor,
                        requestFields(
                                fieldWithPath("phoneNum")
                                        .description("핸드폰 번호")
                                        .attributes(
                                                key("constraints").value("010,011,016,017,018,019으로 시작하는 7, 8 자리 숫자. '-'이 있을경우 제거하고 DB 저장"),
                                                key("format").value("010,011,016,017,018,019으로 시작하는 7, 8 자리 숫자"),
                                                key("pattern").value("^(010|011|016|017|018|019)\\d{7,8}$"),
                                                key("validation").value("@NotBlank @ValidPhoneNumber"),
                                                key("customValidation").value("@ValidPhoneNumber: '-'있을 경우 제거하고 패턴 체크")
                                        )
                        )
                ));
    }

    @Nested
    @DisplayName("인증 코드 확인")
    class VerifyCode {
        SmsVerifyRequest request = new SmsVerifyRequest("01012341234", "123456");
        String requestBody = objectMapper.writeValueAsString(request);
        VerifyCodeResponse response =
                new VerifyCodeResponse(
                        "01012341234",
                        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJzbm1zIiwiaWF0IjoxNzQ4MjY5NDE4LCJleHAiOjE3NDgyNzEyMTh9.9h1Cuq3yNV2yHAzU3K-8glhVjeJpaYKF1xbNTC0oX2dsj5Lmm-ihWHhnBIaiktpkArg4nzsCZXXjj83NEGsmyQ",
                        false,
                        "google",
                        "12345",
                        "test@gmail.com"
                );

        VerifyCode() throws JsonProcessingException {
        }


        @Test
        @DisplayName("[성공] 인증 코드 확인")
        void success_sign_in() throws Exception {
            //given
            //when
            doReturn(response).when(authService).verifyAuthCode(request, "google", "12345", "test@gmail.com");
            //then
            mockMvc.perform(post("/api/auth/verify-code")
                            .params(params)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("auth/verify-code/success",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            queryParameters(
                                    parameterWithName("providerName")
                                            .description("소셜 로그인 제공자 이름")
                                            .optional()
                                            .attributes(
                                                    key("format").value("google, kakao, naver 지원"),
                                                    key("validation").value("@ValidProviderName"),
                                                    key("customValidation").value("@ValidProviderName: null 이거나 google, kakao, naver 일때 통과"),
                                                    key("conditionalRequired").value("소셜 로그인 진행 시 핸드폰 번호에 따른 계정이 없을 때 필수")
                                            ),
                                    parameterWithName("providerUserId")
                                            .description("소셜 로그인 식별자")
                                            .optional()
                                            .attributes(
                                                    key("validation").value("@Nullable @Email"),
                                                    key("conditionalRequired").value("소셜 로그인 진행 시 핸드폰 번호에 따른 계정이 없을 때 필수")
                                            ),
                                    parameterWithName("email")
                                            .description("소셜 로그인 이메일")
                                            .optional()
                                            .attributes(
                                                    key("format").value("email 형식"),
                                                    key("validation").value("@Nullable @Email"),
                                                    key("conditionalRequired").value("소셜 로그인 진행 시 핸드폰 번호에 따른 계정이 없을 때 필수")
                                            )
                            ).and(parameterWithName("_csrf").ignored()),
                            requestFields(
                                    fieldWithPath("phoneNum")
                                            .description("핸드폰 번호")
                                            .attributes(
                                                    key("constraints").value("010,011,016,017,018,019으로 시작하는 7, 8 자리 숫자. '-'이 있을경우 제거하고 DB 저장"),
                                                    key("format").value("010,011,016,017,018,019으로 시작하는 7, 8 자리 숫자"),
                                                    key("pattern").value("^(010|011|016|017|018|019)\\d{7,8}$"),
                                                    key("validation").value("@NotBlank @ValidPhoneNumber"),
                                                    key("customValidation").value("@ValidPhoneNumber: '-'있을 경우 제거하고 패턴 체크")
                                            ),
                                    fieldWithPath("authCode")
                                            .description("문자 인증 코드")
                                            .attributes(
                                                    key("constraints").value("0-9까지 숫자로 이루어진 6자리 인증코드"),
                                                    key("format").value("0-9까지 숫자 6자리"),
                                                    key("pattern").value("^\\d{6}$"),
                                                    key("validation").value("@NotBlank @Pattern")
                                            )
                            ),
                            responseFields(
                                    fieldWithPath("phoneNum").description("사용자 핸드폰 번호"),
                                    fieldWithPath("accessToken")
                                            .description("JWT 엑세스 토큰")
                                            .attributes(
                                                    key("constraints").value("기본적으로 null, 이미 계정이 있다면 엑세스 토큰 반환 후 로그인 처리")
                                            ),
                                    fieldWithPath("needSignUp")
                                            .description("로그인 필요 유무")
                                            .attributes(
                                                    key("constraints").value("소셜 로그인 진행 시 needSignUp 유무 필요")
                                            ),
                                    fieldWithPath("providerName")
                                            .description("소셜 로그인 제공자")
                                            .attributes(
                                                    key("constraints").value("소셜 로그인 진행이 아니라면 null")
                                            ),
                                    fieldWithPath("providerUserId")
                                            .description("소셜 로그인 식별자")
                                            .attributes(
                                                    key("constraints").value("소셜 로그인 진행이 아니라면 null")
                                            ),
                                    fieldWithPath("email")
                                            .description("소셜 로그인 이메일")
                                            .attributes(
                                                    key("constraints").value("소셜 로그인 진행이 아니라면 null")
                                            )
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 인증 코드 확인 - 핸드폰 번호 없음")
        void sign_in_NotFoundMember() throws Exception {
            //given
            //when
            doThrow(new AuthException(NOT_FOUND_PHONENUM)).when(authService).verifyAuthCode(request, null, null, null);
            //then
            mockMvc.perform(post("/api/auth/verify-code")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andDo(document("auth/verify-code/not_found_phonenum",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }
    }

}