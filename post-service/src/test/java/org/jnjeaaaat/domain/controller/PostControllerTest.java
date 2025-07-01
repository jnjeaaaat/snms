package org.jnjeaaaat.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.http.Cookie;
import org.jnjeaaaat.domain.dto.request.CreatePostRequest;
import org.jnjeaaaat.domain.dto.response.CreatePostResponse;
import org.jnjeaaaat.domain.dto.response.PostImageResponse;
import org.jnjeaaaat.domain.dto.response.PostInfoResponse;
import org.jnjeaaaat.domain.service.PostClientService;
import org.jnjeaaaat.domain.service.PostService;
import org.jnjeaaaat.dto.member.MemberInfoResponse;
import org.jnjeaaaat.exception.MemberException;
import org.jnjeaaaat.exception.PostException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.jnjeaaaat.global.cons.FixedData.FIXED_TIME;
import static org.jnjeaaaat.global.cons.FixedData.TEST_IMAGE_FILE_PATH;
import static org.jnjeaaaat.global.exception.ErrorCode.*;
import static org.jnjeaaaat.global.util.UserTestFixture.createTestUser;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "1", roles = "USER")
@WebMvcTest(
        controllers = PostController.class
)
@AutoConfigureRestDocs(outputDir = "../build/generated-snippets")
@ActiveProfiles("test")
class PostControllerTest {

    @MockBean
    private PostService postService;

    @MockBean
    private PostClientService postClientService;

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
    @DisplayName("포스팅 생성")
    class CreatePostTest {

        CreatePostRequest request = new CreatePostRequest("Test Content");
        CreatePostResponse response = CreatePostResponse.builder()
                .id(1L)
                .memberId(1L)
                .build();

        List<MultipartFile> files;
        MockMultipartFile requestPart;

        @BeforeEach
        void init() throws IOException {
            files = List.of(
                    new MockMultipartFile(
                            "files",
                            "profile.png",
                            MediaType.IMAGE_PNG_VALUE,
                            Files.readAllBytes(Paths.get(TEST_IMAGE_FILE_PATH))
                    ),
                    new MockMultipartFile(
                            "files",
                            "profile.png",
                            MediaType.IMAGE_PNG_VALUE,
                            Files.readAllBytes(Paths.get(TEST_IMAGE_FILE_PATH))
                    ),
                    new MockMultipartFile(
                            "files",
                            "profile.png",
                            MediaType.IMAGE_PNG_VALUE,
                            Files.readAllBytes(Paths.get(TEST_IMAGE_FILE_PATH))
                    )
            );

            requestPart = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
            );
        }

        @Test
        @DisplayName("[성공] 포스팅 생성")
        void success_create_post() throws Exception {
            //given
            doReturn(response).when(postService).createPost(
                    createTestUser(),
                    request,
                    files
            );
            //when and then
            mockMvc.perform(multipart("/api/posts")
                            .file(requestPart)
                            .file((MockMultipartFile) files.get(0))
                            .file((MockMultipartFile) files.get(1))
                            .file((MockMultipartFile) files.get(2))
                            .cookie(new Cookie("access_token", "token"))
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("posts/create-post/success",
                            commonResponsePreprocessor,
                            requestParts(
                                    partWithName("request")
                                            .description("포스팅 요청 정보")
                                            .attributes(
                                                    key("constraints").value("content 필드"),
                                                    key("type").value("JSON")
                                            ),
                                    partWithName("files")
                                            .description("포스팅 이미지 파일들")
                                            .attributes(
                                                    key("constraints").value("프로필 사진 이미지 파일"),
                                                    key("max").value("5MB"),
                                                    key("type").value("\"image/jpeg\", \"image/jpg\", \"image/png\", \"image/gif\", \"image/webp\""),
                                                    key("validation").value("@ValidFile"),
                                                    key("customValidation").value("@ValidFile: 파일 하나 크기 5MB, 전체 파일 크기 40MB 제한, 지원하는 타입 제한")
                                            )
                            ),
                            requestFields(
                                    fieldWithPath("content")
                                            .description("포스팅 내용")
                                            .type(STRING)
                                            .attributes(
                                                    key("constraints").value("포스팅 내용은 필수 입력"),
                                                    key("validation").value("@NotBlank, @Size(max = 500)")
                                            )
                            ),
                            responseFields(
                                    fieldWithPath("id")
                                            .description("포스팅 ID")
                                            .type(NUMBER),
                                    fieldWithPath("memberId")
                                            .description("작성자 ID")
                                            .type(NUMBER)
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 포스팅 생성 - 요청 정보가 유효하지 않은 경우")
        void fail_create_post_NotFoundMember() throws Exception {
            //given
            doThrow(new MemberException(NOT_FOUND_MEMBER))
                    .when(postService).createPost(
                            createTestUser(),
                            request,
                            files
                    );
            //when
            //then
            mockMvc.perform(multipart("/api/posts")
                            .file(requestPart)
                            .file((MockMultipartFile) files.get(0))
                            .file((MockMultipartFile) files.get(1))
                            .file((MockMultipartFile) files.get(2))
                            .cookie(new Cookie("access_token", "token"))
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("포스팅 정보 조회")
    class GetPostInfoTest {

        PostInfoResponse response = PostInfoResponse.builder()
                .id(1L)
                .member(MemberInfoResponse.builder()
                        .id(1L)
                        .nickname("Test User")
                        .profileImageUrl("test-profile-image-url")
                        .build())
                .content("Test Content")
                .postImageUrls(List.of(PostImageResponse.builder()
                        .id(1L)
                        .imageUrl("test-image-url")
                        .isThumbnail(true)
                        .build()))
                .createdAt(FIXED_TIME)
                .updatedAt(FIXED_TIME)
                .build();

        @Test
        @DisplayName("[성공] 포스팅 정보 조회")
        void success_get_post_info() throws Exception {
            //given
            doReturn(response).when(postService).getPostInfo(
                    createTestUser(),
                    1L
            );

            //when and then
            mockMvc.perform(get("/api/posts/{postId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("access_token", "token")))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("posts/get-post-info/success",
                            commonResponsePreprocessor,
                            requestCookies(
                                    cookieWithName("access_token")
                                            .optional()
                                            .description("액세스 토큰 인증에 사용됨. 선택적이며 없으면 비인증 상태에서 요청을 처리.")
                            ),
                            pathParameters(
                                    parameterWithName("postId")
                                            .description("포스팅 ID")
                                            .attributes(
                                                    key("constraints").value("포스팅 ID는 필수 입력")
                                            )
                            ),
                            responseFields(
                                    fieldWithPath("id")
                                            .description("포스팅 ID")
                                            .type(NUMBER),
                                    fieldWithPath("member.id")
                                            .description("작성자 ID")
                                            .type(NUMBER),
                                    fieldWithPath("member.nickname")
                                            .description("작성자 닉네임")
                                            .type(STRING),
                                    fieldWithPath("member.profileImageUrl")
                                            .description("작성자 프로필 이미지 URL")
                                            .type(STRING),
                                    fieldWithPath("content")
                                            .description("포스팅 내용")
                                            .type(STRING),
                                    fieldWithPath("postImageUrls[].id")
                                            .description("포스팅 이미지 ID")
                                            .type(NUMBER),
                                    fieldWithPath("postImageUrls[].imageUrl")
                                            .description("포스팅 이미지 URL")
                                            .type(STRING),
                                    fieldWithPath("postImageUrls[].isThumbnail")
                                            .description("포스팅 이미지가 썸네일인지 여부")
                                            .type(BOOLEAN),
                                    fieldWithPath("createdAt")
                                            .description("포스팅 생성 시간")
                                            .type(STRING)
                                            .attributes(
                                                    key("format").value("yyyy-MM-dd HH:mm:ss")
                                            ),
                                    fieldWithPath("updatedAt")
                                            .description("포스팅 수정 시간")
                                            .type(STRING)
                                            .attributes(
                                                    key("format").value("yyyy-MM-dd HH:mm:ss")
                                            )
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 포스팅 정보 조회 - 게시글 없음")
        void get_post_info_NotFoundPost() throws Exception {
            //given
            doThrow(new PostException(NOT_FOUND_POST))
                    .when(postService).getPostInfo(
                            createTestUser(),
                            999L
                    );

            //when and then
            mockMvc.perform(get("/api/posts/{postId}", 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andDo(document("posts/get-post-info/not-found-post",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }

        @Test
        @DisplayName("[실패] 포스팅 정보 조회 - 비공개 게시글")
        void get_post_info_PostNotPublic() throws Exception {
            //given
            doThrow(new PostException(POST_NOT_PUBLIC))
                    .when(postService).getPostInfo(
                            createTestUser(),
                            1L
                    );

            //when and then
            mockMvc.perform(get("/api/posts/{postId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andDo(document("posts/get-post-info/post-not-public",
                            preprocessRequest(prettyPrint()),
                            commonResponsePreprocessor,
                            errorResponseSnippet
                    ));
        }
    }
}