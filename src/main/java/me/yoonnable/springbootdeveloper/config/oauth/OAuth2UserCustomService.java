package me.yoonnable.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.domain.User;
import me.yoonnable.springbootdeveloper.repository.UserRepository;
import me.yoonnable.springbootdeveloper.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    // 리소스 서버(OAuth 서비스)에서 보내주는 사용자 정보를 기반으로 유저 객체를 만들어주는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //  요청을 바탕으로 유저 정부를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest); // 사용자 정보 조회
        saveOrUpdate(user);
        return user;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name)) // 유저가 있으면 엔티티에서 이름만 변경하고
                .orElse(User.builder() // 유저가 없으면 새로운 객체를 생성한 후
                        .email(email)
                        .nickname(name)
                        .build());
        return userRepository.save(user); // users 테이블에 있으면 업데이트, 없으면 인서트
    }
}
