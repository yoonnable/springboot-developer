package me.yoonnable.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.yoonnable.springbootdeveloper.domain.User;
import me.yoonnable.springbootdeveloper.dto.AddUserRequest;
import me.yoonnable.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 패스워드 인코딩용으로 등록한 빈 사용해서 암호화
                .build()).getId();
    }

    // 전달받은 유저 ID로 유저를 검색해서 전달하는 메서드 추가(토큰 API 구현)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
