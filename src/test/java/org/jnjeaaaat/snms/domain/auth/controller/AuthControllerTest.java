package org.jnjeaaaat.snms.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignInRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.exception.DuplicateUidException;
import org.jnjeaaaat.snms.domain.auth.exception.UnmatchedDefaultFile;
import org.jnjeaaaat.snms.domain.auth.exception.UnmatchedPassword;
import org.jnjeaaaat.snms.domain.auth.exception.WrongPassword;
import org.jnjeaaaat.snms.domain.auth.service.AuthService;
import org.jnjeaaaat.snms.domain.member.exception.NotFoundMember;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "USER")
@WebMvcTest(
        controllers = AuthController.class
)
@AutoConfigureRestDocs
@ActiveProfiles("test")
class AuthControllerTest {

    @MockBean
    AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    OperationResponsePreprocessor commonResponsePreprocessor;
    ResponseFieldsSnippet errorResponseSnippet;

    @BeforeEach
    void setUp() {
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
    @DisplayName("로컬 회원 가입")
    class SignUpTest {

        SignUpRequest request = new SignUpRequest("testId",
                "qwER12!@", "qwER12!@", "test1", "010-1234-1234", "default.jpg");
        String requestBody = objectMapper.writeValueAsString(request);
        SignUpResponse response = new SignUpResponse(1L);

        SignUpTest() throws JsonProcessingException {
        }


        @Test
        @DisplayName("[성공] 로컬 회원 가입")
        void success_sign_up() throws Exception {
            //given
            //when
            doReturn(response).when(authService).signUp(request);
            //then
            mockMvc.perform(post("/api/auth/sign-up")
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
                            requestFields(
                                    fieldWithPath("uid").description("가입 아이디"),
                                    fieldWithPath("password").description("비밀번호"),
                                    fieldWithPath("confirmPassword").description("비밀번호 확인"),
                                    fieldWithPath("nickname").description("닉네임"),
                                    fieldWithPath("phoneNum").description("핸드폰 번호"),
                                    fieldWithPath("profileImgUrl").description("기본 프로필 이미지")
                            ),
                            responseFields(
                                    fieldWithPath("id").description("사용자 고유 ID")
                            )
                    ));

            verify(authService, times(1)).signUp(any(SignUpRequest.class));
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 중복 아이디")
        void sign_up_DuplicatedUid() throws Exception {
            //given
            //when
            doThrow(new DuplicateUidException()).when(authService).signUp(request);
            //then
            mockMvc.perform(post("/api/auth/sign-up")
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

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 비밀번호 불일치")
        void sign_up_UnmatchedPassword() throws Exception {
            //given
            //when
            doThrow(new UnmatchedPassword()).when(authService).signUp(request);
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

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 기본 프로필 사진 불일치")
        void sign_up_UnmatchedDefaultFile() throws Exception {
            //given
            //when
            doThrow(new UnmatchedDefaultFile()).when(authService).signUp(request);
            //then
            mockMvc.perform(post("/api/auth/sign-up")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("auth/sign-up/unmatched_default_file",
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
                new SignInResponse("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJzbm1zIiwiaWF0IjoxNzQ4MjY5NDE4LCJleHAiOjE3NDgyNzEyMTh9.9h1Cuq3yNV2yHAzU3K-8glhVjeJpaYKF1xbNTC0oX2dsj5Lmm-ihWHhnBIaiktpkArg4nzsCZXXjj83NEGsmyQ");

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
                                    fieldWithPath("uid").description("사용자 아이디"),
                                    fieldWithPath("password").description("사용자 비밀번호")
                            ),
                            responseFields(
                                    fieldWithPath("accessToken").description("엑세스 토큰")
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 로컬 로그인 - 회원 없음")
        void sign_in_NotFoundMember() throws Exception {
            //given
            //when
            doThrow(new NotFoundMember()).when(authService).signIn(request);
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
            doThrow(new WrongPassword()).when(authService).signIn(request);
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

}