package me.yoonnable.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yoonnable.springbootdeveloper.domain.Article;
import me.yoonnable.springbootdeveloper.dto.AddArticleRequest;
import me.yoonnable.springbootdeveloper.dto.UpdateArticleRequest;
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

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        //given : 블로그 글을 저장합니다.
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";

        blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when : 목록 조회 API를 호출합니다.
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        //then : 응답 코드가 200 OK이고, 반환받은 값 중에 0번째 요소의 content와 title이 저장된 값과 같은지 확인합니다.
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        //given : 블로그 글을 저장합니다.
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when : 저장한 블로그 글의 id값으로 API를 호출합니다.
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        //then : 응답 코드가 200 OK이고, 반환받은 content와 title이 저장된 값과 같은지 확인합니다.
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        //given : 블로그 글을 저장한다.
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        //when : 저장한 블로그 글의 id값으로 삭제 API를 호출한다.
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        //then : 응답 코드가 200 OK이고, 블로그 글 리스트를 전체 조회해 조회한 배열 크기가 0인지 확인한다.
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        //given : 블로그 글을 저장하고, 블로그 글 수정에 필요한 요청 객체를 만듭니다.
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when : UDATE API로 수정 요청을 보냅니다. 이때 요청 타입은 JSON이며, given절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냅니다.
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        //Then : 응답 코드가 200 OK인지 확인합니다. 블로그 글 id로 조회한 후에 값이 수정되었는지 확인합니다.
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }
}