package me.yoonnable.springbootdeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController { // 로그인, 회원 가입 경로로 접근하면 뷰 파일을 연결하는 컨트롤러

    @GetMapping("/login")
    public String login() {
//        return "login"; // 로그인 뷰 파일 이동
        return "oauthLogin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // 회원가입 뷰 파일 이동
    }
}
