package me.yoonnable.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.domain.Article;
import me.yoonnable.springbootdeveloper.dto.AddArticleRequest;
import me.yoonnable.springbootdeveloper.dto.ArticleResponse;
import me.yoonnable.springbootdeveloper.dto.UpdateArticleRequest;
import me.yoonnable.springbootdeveloper.repository.BlogRepository;
import me.yoonnable.springbootdeveloper.service.BlogService;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;

    //HTTP 메서드가 POST일 때 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    //@RequestBody 로 요청 본문 값 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request,
                                              Principal principal) { //현재 인증 정보를 가져오는 principal 객체(java.security)
        Article saveArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED) // 응답코드 201로 Created를 응답하고 테이블에 저장된 객체를 반환
                .body(saveArticle);
    }
    
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticle() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new) // 앞에 stream 안에 있는 Article 객체들을 ArticleResponse의 생성자를 이용해 파싱
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    // URL 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request) {
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updateArticle);
    }
}
