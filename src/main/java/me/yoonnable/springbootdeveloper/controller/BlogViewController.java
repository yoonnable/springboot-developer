package me.yoonnable.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.domain.Article;
import me.yoonnable.springbootdeveloper.dto.ArticleListViewResponse;
import me.yoonnable.springbootdeveloper.dto.ArticleViewResponse;
import me.yoonnable.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {
    
    private final BlogService blogService;
    
    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles); //블로그 글 리스트 저장
        
        return "articleList"; //articleList.html라는 뷰 조회
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) { // id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음)
        if(id == null) { // id가 없으면 생성
            model.addAttribute("article", new ArticleViewResponse()); //빈 ArticleViewResponse 객체 생성
        } else { // id가 없으면 수정
            Article article = blogService.findById(id); // 기존 값을 가져옴
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }
}
