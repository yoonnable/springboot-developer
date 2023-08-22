package me.yoonnable.springbootdeveloper.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.config.jwt.TokenProvider;
import me.yoonnable.springbootdeveloper.domain.RefreshToken;
import me.yoonnable.springbootdeveloper.domain.User;
import me.yoonnable.springbootdeveloper.repository.RefreshTokenRepository;
import me.yoonnable.springbootdeveloper.service.UserService;
import me.yoonnable.springbootdeveloper.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

// 스프링 시큐리티의 기본 로직에서는 별도의 authenticationSuccessHandler를 지정하지 않으면
// 로그인 성공 이후 default로 SimpleUrlAuthenticationSuccessHandler를 사용.
// 우리는 토큰과 관련된 작업을 추가로 처리하기 위해 커스텀 Success Handler 클래스 작성.
// SimpleUrlAuthenticationSuccessHandler를 상속받고,
// onAuthenticationSuccess() 메서드를 오버라이드해서 재정의
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    // 로그인 성공 직후 처리하고 싶은 기능 구현
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("성공 핸들러 실행 시작");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("OAuth2User : " + oAuth2User);
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        System.out.println("DB에 저장된 user : " + user);

        // 1. 리프레시 토큰 설정 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION); // 토큰제공자를 사용해 리프레시 토큰 생성
        saveRefreshToken(user.getId(), refreshToken); // 생성한 리프레시토큰을 사용자 아이디와 함께 DB에 저장
        addRefreshTokenToCookie(request, response, refreshToken);// 쿠키에도 리프레시 토큰 저장(이후 클라이언트에서 액세스 토큰 만료 시 재발급 요청위해)

        // 2. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION); // 토큰제공자로 액세스토큰 생성
        String targetUrl = getTargetUrl(accessToken); // 리다이렉트 경로가 담긴 값의 쿼리 파라미터에 액세스 토큰 추가한 값

        // 3. 인증 관련 설정값, 쿠키 제거
        // 인증 프로세스 과정 중 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터들 제거
        clearAuthenticationAttributes(request, response);

        // 4. 리다이렉트 : 2번에서 만든 값(=패스=URL)로 리다이렉트 시킨다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        // 부모 클래스에서 기본적으로 제공하는 clearAuthenticationAttributes() 메서드는 그대로 호출
        super.clearAuthenticationAttributes(request);
        // OAuth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies() 메서드를 추가로 호출해
        // OAuth 인증을 위해 저장된 정보도 삭제
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)// 쿠키에서 리다이렉트 경로가 담긴 값을 가져와
                .queryParam("token", token)// 쿼리 파라미터에 엑세스 토큰 추가
                .build()
                .toUriString();
    }
}
