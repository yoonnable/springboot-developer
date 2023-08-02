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
}
