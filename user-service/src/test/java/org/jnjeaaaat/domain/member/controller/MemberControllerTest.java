package org.jnjeaaaat.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.http.Cookie;
import org.jnjeaaaat.domain.member.dto.request.UpdateMemberRequest;
import org.jnjeaaaat.domain.member.dto.response.UpdateMemberResponse;
import org.jnjeaaaat.domain.member.service.MemberService;
import org.jnjeaaaat.exception.MemberException;
import org.jnjeaaaat.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.jnjeaaaat.global.cons.FixedData.FIXED_TIME;
import static org.jnjeaaaat.global.cons.FixedData.TEST_IMAGE_FILE_PATH;
import static org.jnjeaaaat.global.exception.ErrorCode.*;
import static org.jnjeaaaat.global.util.UserTestFixture.createTestUser;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "1", roles = "USER")
@WebMvcTest(
        controllers = MemberController.class
)
@AutoConfigureRestDocs(outputDir = "../build/generated-snippets")
@ActiveProfiles("test")
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    OperationResponsePreprocessor commonResponsePreprocessor;
    ResponseFieldsSnippet errorResponseSnippet;

    @BeforeEach
    void setUp() {

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 날짜 포맷 설정
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(module);

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
                fieldWithPath("errorCode")
                        .description("에러 상태")
                        .type(STRING),
                fieldWithPath("message")
                        .description("에러 메세지")
                        .type(STRING)
        );
    }

    @Nested
    @DisplayName("사용자 정보 업데이트")
    class UpdateMemberInfo {

        UpdateMemberRequest request = new UpdateMemberRequest(
                "새로운닉네임",
                "첫 자기소개 작성"
        );

        UpdateMemberResponse response = new UpdateMemberResponse(
                1L,
                "새로운닉네임",
                "https://s3.amazonaws.com/bucket/image.jpg",
                false,
                "첫 자기소개 작성",
                FIXED_TIME
        );

        MockMultipartFile imageFile;
        MockMultipartFile requestPart;

        @BeforeEach
        void init() throws IOException {
            imageFile = new MockMultipartFile(
                    "file",
                    "profile.png",
                    MediaType.IMAGE_PNG_VALUE,
                    Files.readAllBytes(Paths.get(TEST_IMAGE_FILE_PATH))
            );

            requestPart = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
            );
        }

        @Test
        @DisplayName("[성공] 사용자 정보 업데이트")
        void success_update_member_info() throws Exception {
            //given
            doReturn(response).when(memberService).updateMemberInfo(
                    createTestUser(),
                    1L,
                    request,
                    imageFile
            );
            //when
            //then
            mockMvc.perform(multipart("/api/members/{memberId}", 1)
                            .file(requestPart)
                            .file(imageFile)
                            .cookie(new Cookie("access_token", "token"))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("members/updateMemberInfo/success",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            pathParameters(
                                    parameterWithName("memberId")
                                            .description("사용자 id")
                                            .attributes(
                                                    key("type").value("NUMBER")
                                            )
                            ),
                            requestParts(
                                    partWithName("request")
                                            .description("수정할 회원 정보(JSON)")
                                            .optional()
                                            .attributes(
                                                    key("constraints").value("nickname, introduce 필드"),
                                                    key("type").value("JSON"),
                                                    key("conditionalRequired").value("null 을 보내고 싶을땐 {} 으로 전송")
                                            ),
                                    partWithName("file")
                                            .description("프로필 이미지 파일")
                                            .optional()
                                            .attributes(
                                                    key("constraints").value("프로필 사진 이미지 파일"),
                                                    key("max").value("5MB"),
                                                    key("type").value("\"image/jpeg\", \"image/jpg\", \"image/png\", \"image/gif\", \"image/webp\""),
                                                    key("validation").value("@ValidFile"),
                                                    key("customValidation").value("@ValidFile: 파일 하나 크기 5MB, 전체 파일 크기 40MB 제한, 지원하는 타입 제한")
                                            )
                            ),
                            requestFields(
                                    fieldWithPath("nickname")
                                            .description("사용자 닉네임")
                                            .type(STRING)
                                            .optional()
                                            .attributes(
                                                    key("constraints").value("1-10자, 영문 대소문자, 숫자, 한글 조합"),
                                                    key("minLength").value("1"),
                                                    key("maxLength").value("10"),
                                                    key("format").value("영문 대소문자, 숫자, 한글 조합"),
                                                    key("pattern").value("^[a-zA-Z0-9가-힣]{1,10}$"),
                                                    key("validation").value("@Nullable @Pattern")
                                            ),
                                    fieldWithPath("introduce")
                                            .description("자기소개")
                                            .type(STRING)
                                            .optional()
                                            .attributes(
                                                    key("constraints").value("사용자 자기소개 500자 까지 작성 가능"),
                                                    key("maxLength").value("500"),
                                                    key("validation").value("@Nullable @Size")
                                            )
                            ),
                            responseFields(
                                    fieldWithPath("id")
                                            .description("사용자 고유 ID")
                                            .type(NUMBER),
                                    fieldWithPath("nickname")
                                            .description("업데이트된 닉네임")
                                            .type(STRING),
                                    fieldWithPath("profileImgUrl")
                                            .description("업데이트된 프로필 사진 url")
                                            .type(STRING),
                                    fieldWithPath("defaultProfileImg")
                                            .description("default Img 인지 체크")
                                            .type(BOOLEAN),
                                    fieldWithPath("introduce")
                                            .description("업데이트된 자기소개")
                                            .type(STRING),
                                    fieldWithPath("updatedAt")
                                            .description("업데이트 날짜")
                                            .type(STRING)
                                            .attributes(
                                                    key("format").value("yyyy-MM-dd HH:mm:ss")
                                            )
                            )
                    ));

        }

        @Test
        @DisplayName("[실패] 사용자 정보 업데이트 - 존재하지 않는 계정")
        void update_member_info_NotFoundMember() throws Exception {
            //given
            doThrow(new MemberException(NOT_FOUND_MEMBER)).when(memberService).updateMemberInfo(
                    createTestUser(),
                    1L,
                    request,
                    imageFile
            );
            //when
            //then
            mockMvc.perform(multipart("/api/members/{memberId}", 1)
                            .file(requestPart)
                            .file(imageFile)
                            .cookie(new Cookie("access_token", "token"))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andDo(document("members/updateMemberInfo/not_found_member",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

        @Test
        @DisplayName("[실패] 사용자 정보 업데이트 - 삭제 처리된 계정")
        void update_member_info_AlreadyDeletedMember() throws Exception {
            //given
            doThrow(new MemberException(ALREADY_DELETED_MEMBER)).when(memberService).updateMemberInfo(
                    createTestUser(),
                    1L,
                    request,
                    imageFile
            );
            //when
            //then
            mockMvc.perform(multipart("/api/members/{memberId}", 1)
                            .file(requestPart)
                            .file(imageFile)
                            .cookie(new Cookie("access_token", "token"))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("members/updateMemberInfo/already_deleted_member",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

        @Test
        @DisplayName("[실패] 사용자 정보 업데이트 - 토큰 사용자 불일치")
        void update_member_info_AuthenticationUserMismatch() throws Exception {
            //given
            doThrow(new MemberException(AUTHENTICATION_MEMBER_MISMATCH)).when(memberService).updateMemberInfo(
                    createTestUser(),
                    2L,
                    request,
                    imageFile
            );
            //when
            //then
            mockMvc.perform(multipart("/api/members/{memberId}", 2)
                            .file(requestPart)
                            .file(imageFile)
                            .cookie(new Cookie("access_token", "token"))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andDo(document("members/updateMemberInfo/authentication_user_mismatch",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }
    }


}