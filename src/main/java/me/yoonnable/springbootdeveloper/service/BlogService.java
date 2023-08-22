package me.yoonnable.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.domain.Article;
import me.yoonnable.springbootdeveloper.dto.AddArticleRequest;
import me.yoonnable.springbootdeveloper.dto.UpdateArticleRequest;
import me.yoonnable.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가 (lombok)
@Service //Bean 등록
public class BlogService {

    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드
    public Article save(AddArticleRequest request, String userName) { // user이름을 추가로 입력받고 인수로 전달
        return blogRepository.save(request.toEntity(userName)); // 인수로 전달받은 유저 이름을 반환
    }

    //블로그 글 목록 조회 메서드
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    //블로그 글 조회 메서드
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        //id로 엔티티를 조회하고 없으면 IllegalArgumentException 예외를 발생시킨다.
    }

    //블로그 글 삭제 메서드
    public void delete(long id) {
//        blogRepository.deleteById(id);

        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article); // 게시글을 작성한 유저인지 확인
        blogRepository.delete(article); // 객체를 인자로 삭제하네~
    }

    //블로그 글 수정 메서드
    @Transactional // 트랜잭션 메서드
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article); // 게시글을 작성한 유저인지 확인
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 인증 서버에서 유저네임 가져옴
        // 만약 서로 다르면 예외를 발생시켜 작업을 수행하지 않음!!!!
        if(!article.getAuthor().equals(userName)) throw new IllegalArgumentException("not authorized");
    }
}
