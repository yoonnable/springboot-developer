package me.yoonnable.springbootdeveloper.util;

import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    // 요청값(이름, 값, 만료 기간)을 바탕으로 HTTP 응답에 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 쿠키의 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return;

        // 실제로 삭제하는 벙법은 없으므로 파라미터로 넘어온 키의 쿠키를 빈 값으로 바꾸고
        // 만료시간을 0으로 설정해 쿠키가 재생되자마자 만료 처리
        for(Cookie cookie : cookies) {
            if(name.equals(cookie.getName())) {
                cookie.setValue(""); // 빈 값으로 바꾸고
                cookie.setPath("/");
                cookie.setMaxAge(0); // 만료처리
                response.addCookie(cookie);
            }
        }
    }

    // 객체를 직렬화해 쿠키의 값으로 대입할 문자열로 변환
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키를 역직렬화해 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
