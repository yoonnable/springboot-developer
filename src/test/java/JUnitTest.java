import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JUnitTest {
    @DisplayName("1 + 2는 3이다") //테스트 이름 명시
    @Test //테스트를 수행하는 메서드로 만들어줌 -> 다른 테스트에 영향을 주지 않고 독립적
    public void junitTest() {
        int a = 1;
        int b = 2;
        int sum = 3;

        Assertions.assertEquals(sum, a + b); //두 인수 값이 같은지 확인
        //첫 번째 인수 : 기대 값
        //두 번째 인수 : 검증할 값
    }
    @DisplayName("1 + 3는 4이다") //테스트 이름 명시
    @Test //테스트를 수행하는 메서드로 만들어줌 -> 다른 테스트에 영향을 주지 않고 독립적
    public void junitFailedTest() {
        int a = 1;
        int b = 3;
        int sum = 3;

        Assertions.assertEquals(sum, a + b); //두 인수 값이 같은지 확인
        //첫 번째 인수 : 기대 값
        //두 번째 인수 : 검증할 값
    }
}
