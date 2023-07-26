package me.yoonnable.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yoonnable.springbootdeveloper.domain.Article;

@NoArgsConstructor //기본 생성자 추가
@AllArgsConstructor // 모든 필드값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest { // 서비스에서 요청을 받을 객체

    private String title;
    private String content;

    public Article toEntity() { // 생성자를 사용해 객체 생성
        //toEntity() : 빌더 패턴을 사용해 DTO를 엔티티로 만들어주는 메서드이다.
        // -> 추후 블로그 글을 추가할 떄 저장할 엔티티로 변환 용도로 사용
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}
