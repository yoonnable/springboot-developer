package me.yoonnable.springbootdeveloper;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본생성자 (접근 제어자 = protected) 엔티티의 필수
@AllArgsConstructor
@Getter
@Entity // JPA가 관리하는 객체로 지정 = 엔티티
public class Member {// member라는 이름의 테이블에 접근하는데 사용할 객체
    @Id // 기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성방식 결정 (자동 증가 = auto_increment)
    @Column(name = "id", updatable = false)
    private Long id; //DB 테이블의 'id' 컬럼과 매칭
    
    @Column(name = "name", nullable = false)
    private String name; //DB 테이블의 'name' 컬럼과 매칭
}
