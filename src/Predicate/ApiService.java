package Predicate;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 3. 서비스를 사용하는 클래스
// 문제 3 : "예외의 의미 있는 변환"
public class ApiService {
    private final ApiClient apiClient;

    public ApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // [문제 3번 대상 코드]
    // 목표: catch 블록을 switch 식 등을 활용해 모던하게 리팩토링
    public String callExternalApi() throws Exception {
        /*
        throw switch (apiClient.call()) {
            case "타임아웃 발생" -> new TimeoutException();
            case "치명적 오류 발생" ->  new FatalException("치명적 오류", new Throwable());
            default -> new Exception();
        };
         */

        try {
            return apiClient.call();
        } catch (TimeoutException | IOException | IllegalArgumentException e) {
            throw switch (e) {
                case TimeoutException t -> new RetryableException("일시적 오류 발생", t);
                default -> new FatalException("복구 불가능한 시스템 오류", e);
            };
        }
    }
}