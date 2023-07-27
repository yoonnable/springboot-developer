package me.yoonnable.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yoonnable.springbootdeveloper.domain.Article;
import me.yoonnable.springbootdeveloper.dto.AddArticleRequest;
import me.yoonnable.springbootdeveloper.repository.BlogRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest // 테스트용 애플리케이션 컨텍스트
    @AutoConfigureMockMvc //MockMvc 생성 및 자동 구성
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc; // 애플리케이션을 서버에 배포하지 않고도 테스트용 MVC 환경을 만들어 요청, 전송, 응답 기능을 제공하는 유틸리티 클래스

    @Autowired
    protected ObjectMapper objectMapper; // 직렬화, 역직렬화를 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach //테스트 실행 전 실행하는 메서드
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll(); // 저장되어 있는 데이터가 있다면, 다 지우고 시작!
        // MockMvcBuilders : mockMvc 설정하는 클래스 (메서드 2개 가지고 있음)
        // M1. standaloneSetup() : 수동으로 생성하고 구성한 컨트롤러 한 개 이상을 서비스할 Mock MVC를 만든다.
        // M2. webAppContextSetup() : 구성된 컨트롤러 한 개 이상을 포함하는 스프링 애플리케이션 컨텍스트를 사용하여 Mock MVC를 만든다.
        //                            (스프링이 로드한) WebApplicationContext의 인스턴스로 작동한다
    }

    @DisplayName(("addArticle: 블로그 글 추가에 성공한다."))
    @Test
    public void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // 객체 -> JSON 으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        //when
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url) // mockMvc을 사용해 HTTP 메서드, url 설정
                .contentType(MediaType.APPLICATION_JSON_VALUE) // mockMvc을 사용해 요청 타입 설정
                .content(requestBody)); // mockMvc을 사용해 요청 본문 설정
        // contentType() 메서드 : 요청을 보낼 때 JSON, XML 등 다양한 타입 중 하나를 골라 요청을 보냄
        // 여기선 JSON 타입의 요청을 보낸다고 명시

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1); // 크기가 1인지 검증
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }
}