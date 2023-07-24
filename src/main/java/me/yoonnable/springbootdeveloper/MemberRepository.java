package me.yoonnable.springbootdeveloper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> { //member라는 이름의 테이블에 접근해서 Memeber 클래스에 매핑하는 구현체
//JpaRepository를 상속 받을 때 엔티티 타입의 Member와 엔티티의 기본키 타입 Long을 인수로(제네릭) 넣어준다.
}
