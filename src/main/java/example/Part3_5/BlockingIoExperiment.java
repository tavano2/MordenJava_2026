package example.Part3_5;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * [Week 1 - 1교시 - Step 2]
 * Legacy I/O의 Blocking 현상을 증명하는 실험 코드
 */
public class BlockingIoExperiment {

    // Part 3에서 배운 Sealed Interface (Java 17 표준)
    // I/O의 결과를 타입 안전하게 관리하기 위해 사용해.
    sealed interface IoTaskResult permits Success, Failure {}
    record Success(int taskId, String threadName, long duration) implements IoTaskResult {}
    record Failure(int taskId, String threadName, Throwable cause) implements IoTaskResult {}

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[%s] 실험 시작 - 스레드 풀 크기: 2, 총 작업: 6".formatted(LocalTime.now()));

        // 1. 딱 2개의 스레드만 있는 풀 생성 (병목 현상을 극대화해서 관찰하기 위함)
        ExecutorService executor = Executors.newFixedThreadPool(2);
        long startTime = System.currentTimeMillis();

        // 2. 6개의 I/O 집약적 작업을 제출
        IntStream.rangeClosed(1, 6).forEach(i -> {
            executor.submit(() -> {
                IoTaskResult result = performBlockingIo(i);
                printResult(result);
            });
        });

        // 3. 모든 작업이 끝날 때까지 대기
        executor.shutdown();
        if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
            long totalDuration = System.currentTimeMillis() - startTime;
            System.out.println("--------------------------------------------------");
            System.out.println("[%s] 모든 실험 종료!".formatted(LocalTime.now()));
            System.out.println("전체 소요 시간: " + totalDuration + "ms");
            System.out.println("결과 분석: 스레드는 2개인데 각 1초씩 걸리는 작업 6개를 처리하느라 약 3초가 소요됨.");
        }
    }

    /**
     * 전통적인 java.io 방식으로 데이터를 읽는 상황을 시뮬레이션해.
     * Thread.sleep()은 OS 레벨에서 스레드가 Blocked 상태가 되는 것과 매우 유사하게 동작해.
     */
    private static IoTaskResult performBlockingIo(int taskId) {
        String threadName = Thread.currentThread().getName();
        try {
            // [Under the hood]
            // 실제 상황이라면 inputStream.read()가 호출되어 OS의 응답을 기다리는 구간이야.
            // 이 순간 이 스레드는 아무런 연산을 하지 못하고 CPU를 점유하지 못한 채 대기(Wait) 상태로 빠져.
            Thread.sleep(1000);

            return new Success(taskId, threadName, 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Failure(taskId, threadName, e);
        }
    }

    private static void printResult(IoTaskResult result) {
        // Java 17의 Pattern Matching for instanceof 사용 (실무 지향 문법)
        if (result instanceof Success(int taskId, String threadName, long duration)) {
            System.out.printf("[%s] [작업 %d] 완료 - 실행 스레드: %s (소요: %dms)%n",
                    LocalTime.now(), taskId, threadName, duration);
        } else if (result instanceof Failure(int taskId, String threadName, Throwable cause)) {
            System.err.printf("[%s] [작업 %d] 실패 - 스레드: %s, 이유: %s%n",
                    LocalTime.now(), taskId, threadName, cause.getMessage());
        }
    }
}
