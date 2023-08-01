package me.yoonnable.springbootdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // created_at, updated_at 자동 업데이트
@SpringBootApplication //spring boot의 기본 설정을 해준다
public class SpringBootDeveloperApplication { //이 프로젝트에서 메인 클래스로 사용 할 것!!
    public static void main(String[] args) { //메인 메소드
        // SpringApplication.run() <- 애플리케이션을 실행
        // 첫 번째 인수 : 스프링 부트 3 애플리케이션의 메인 클래스로 사용할 클래스
        // 두 번째 인수 : 커맨드 라인의 인수들을 전달
        SpringApplication.run(SpringBootDeveloperApplication.class, args);
    }
}
