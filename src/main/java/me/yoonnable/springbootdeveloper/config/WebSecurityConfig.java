package me.yoonnable.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    //스프링 시큐리티 기능 비활성화(모든 곳에 적용하지 않음)
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) // h2데이터 확인하는 h2-console 하위 url 스프링 시큐리티 비활성화
                .requestMatchers("/static/**"); // 정적 리소스 스프링 시큐리티 비활성화
    }

    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests() // 인증, 인가 설정
                .requestMatchers("/login", "/signup", "/user").permitAll() // /login, /signup, /user로 요청이 오면 인증/인가 없이도 접근 가능
                .anyRequest().authenticated() //위 url 이외의 요청에 대해 설정 . 별도의 인가는 필요 없고, 인등이 성공되어야 접근 가능
                .and()
                .formLogin() // 폼 기반 로그인 설정
                .loginPage("/login") //로그인 페이지 경로 설정
                .defaultSuccessUrl("/articles") //로그인이 완료되었을 때 이동할 경로 설정
                .and()
                .logout() //로그아웃 설정
                .logoutSuccessUrl("/login")// 로그아웃이 완료되었을 때 이동할 경로 설정
                .invalidateHttpSession(true)//로그아웃 이후 세션을 전체 삭제할지 여부 설정
                .and()
                .csrf().disable() //csrf 비활성화(CSRF 공격을 방지하기 위해선 활성화 하는게 좋음)
                .build();
    }

    // 인증 관리자 관련 설정 : 사용자 정보 가져올 서비스 재정의, 인증 방법(LDAP, JDBC 기반 인증 등) 설정
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService) // 사용자 정보를 가져올 서비스 설정(UserDetailsService를 상속받은 클래스여야 함)
                .passwordEncoder(bCryptPasswordEncoder) // 비밀번호 암호화하기 위한 인코더 설정
                .and()
                .build();
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
