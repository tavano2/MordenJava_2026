package example.Predicate;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 1. 외부 API 인터페이스 (Legacy)
interface ApiClient {
    /**
     * @throws TimeoutException : 일시적인 네트워크 지연 (재시도 가능)
     * @throws IOException : 네트워크 단절 등 심각한 IO 오류 (복구 불가)
     * @throws IllegalArgumentException : 잘못된 요청 인자 (복구 불가)
     */
    String call() throws TimeoutException, IOException, IllegalArgumentException;
}

// 2. 우리가 던져야 할 커스텀 예외 (New)
// 재시도 가능한 예외
class RetryableException extends RuntimeException {
    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}

// 복구 불가능한 치명적 예외
class FatalException extends RuntimeException {
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }
}


