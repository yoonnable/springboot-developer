package me.yoonnable.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yoonnable.springbootdeveloper.config.jwt.JwtFactory;
import me.yoonnable.springbootdeveloper.config.jwt.JwtProperties;
import me.yoonnable.springbootdeveloper.domain.RefreshToken;
import me.yoonnable.springbootdeveloper.domain.User;
import me.yoonnable.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.yoonnable.springbootdeveloper.repository.RefreshTokenRepository;
import me.yoonnable.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest { // 테스트 코드의 파일명은 테스트 할 파일명Test 이다.
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        //given : 테스트 유저 생성, jjwt 라이브러리를 이용해 리프레시 토큰을 만들어 데이터베이스에 저장.
        // 토큰 생성 API의 요청 본문에 리프레시 토큰을 포함하여 요청 객체를 생성
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        //when : 토큰 추가 API에 요청을 보낸다.
        // 요청 타입 : JSON, given절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냄.
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then : 응답 코드가 201 Created 인지 확인하고 응답으로 온 액세스 토큰이 비어있지 않은지 확인.
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
