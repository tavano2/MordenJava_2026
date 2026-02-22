package Part3;

public class SealedClassDeepDive {

    // --- 1. Domain Model (DOP Style) ---
    // "이 API 응답은 오직 Success, Failure, Timeout 세 가지만 존재한다"고 언어 차원에서 선언 (봉인)
    public sealed interface ApiResponse permits Success, Failure, Timeout {}

    // 하위 타입들은 상태가 변하지 않는 순수 데이터(Record)로 정의
    public record Success(String data, int statusCode) implements ApiResponse {}
    public record Failure(String errorCode, String errorMessage) implements ApiResponse {}
    public record Timeout(int retryCount) implements ApiResponse {}

    // 🚨 [실험 1] 아래 주석을 해제해 보게!
    // permits에 명시되지 않은 클래스가 구현하려고 하면 즉시 컴파일 에러 발생
    // 에러 메시지: "class is not allowed to extend sealed class"
    // public record UnknownResponse(String raw) implements ApiResponse {}


    // --- 2. Business Logic (Service Layer) ---
    // 데이터(ApiResponse)에는 로직이 전혀 없고, 서비스 로직이 데이터를 패턴 매칭으로 처리함
    public static void handleResponseLegacy(ApiResponse response) {
        System.out.println("--- Legacy (Java 8) 방식 ---");
        // 과거에는 다형성을 안 쓰면 이렇게 지저분한 캐스팅 지옥이 펼쳐졌지.
        if (response instanceof Success) {
            Success s = (Success) response;
            System.out.println("✅ 처리 완료: " + s.data());
        } else if (response instanceof Failure) {
            Failure f = (Failure) response;
            System.out.println("❌ 실패 로직 처리: " + f.errorMessage());
        } else if (response instanceof Timeout) {
            Timeout t = (Timeout) response;
            System.out.println("⏳ 재시도 큐로 전송. 현재 시도 횟수: " + t.retryCount());
        } else {
            // 누군가 UnknownResponse를 만들었을까 봐 두려워하며 적는 방어 로직
            throw new IllegalArgumentException("알 수 없는 타입입니다.");
        }
    }



    public static void main(String[] args) {
        ApiResponse apiData = new Success("{\"user\": \"DevKim\", \"balance\": 50000}", 200);

        handleResponseLegacy(apiData);
    }


}
