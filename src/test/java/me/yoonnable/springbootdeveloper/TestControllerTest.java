package me.yoonnable.intro;

import me.yoonnable.intro.Member;
import me.yoonnable.intro.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 메인 애플리케이션 클래스에 추가하는 애너테이션인 @SpringBootApplication이 있는 클래스를 찾고 그 클래스에 포함되어있는 빈을 찾은 다음, 테스트용 애플리케이션 컨택스트 생성
    @AutoConfigureMockMvc // MockMvc(애플리케이션을 서버에 배포하지 않고도 테스트용 MVC 환경을 만들어 요청, 전송, 응답 기능을 제공하는 유틸리티 클래스) 생성 및 자동 구성
class TestControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @BeforeEach // 태스트 실행 전 실행하는 메서드
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    
    @AfterEach // 태스트 실행 후 실행하는 메서드 - member 테이블에 있는 데이터들을 모두 삭제해줌
    public void cleanUp() {
        memberRepository.deleteAll();
    }

    @DisplayName("getAllMembers : 아티클 조회에 성공한다.")
    @Test
    public void getAllMembers() throws Exception {
        // given : 테스트 준비 과정
        // 멤버를 저장합니다.
        final String url = "/test";
        Member saveMember = memberRepository.save(new Member(1L, "홍길동"));

        // when : 실제 테스트 실행
        // 멤버 리스트를 조회하는 API를 호출합니다.
        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        // then : 테스트 결과가 잘 되었는지 검증
        // 응답 코드가 200 OK이고, 반환받은 값 중에 0번째 요소의 id와 name이 저장된 값과 같은지 확인합니다.
        result.andExpect((status().isOk()))
                // 응답의 0번째 값이 DB에 저장한 값과 같은지 확인
                .andExpect(jsonPath("$[0].id").value(saveMember.getId()))
                .andExpect(jsonPath("$[0].name").value(saveMember.getName()));
    }

}