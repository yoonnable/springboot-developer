package me.yoonnable.springbootdeveloper.repository;

import me.yoonnable.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
     Optional<User> findByEmail(String email); // email로 사용자 정보를 가져옴
    // findByEmail() 메서드가 요청하는 쿼리 예
    // FROM users WHERE email = #{email}
}
