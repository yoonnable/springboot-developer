package me.yoonnable.springbootdeveloper.repository;

import me.yoonnable.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
