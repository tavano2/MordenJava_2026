package example.Part3_5;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * [Week 1 - 1교시 - Step 3]
 * 실전 챌린지: 레거시 I/O 모니터링의 한계 체험
 */
public class LegacyIoChallenge {

    // Java 17 Sealed Interface로 작업 상태 정의
    sealed interface WorkerStatus permits Running, Done {}
    record Running(String threadName, LocalTime startTime) implements WorkerStatus {}
    record Done(String threadName, long duration) implements WorkerStatus {}

    // 1. 작업 정보를 담을 간단한 로컬 레코드 (Java 17)
    record TaskInfo(int id, long startTime, Future<Done> future) {}

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Done>> futures = new ArrayList<>();

        System.out.println("[%s] 로그 수집 시작...".formatted(LocalTime.now()));

        List<TaskInfo> taskList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            long start = System.currentTimeMillis();
            // executor.submit()이 반환하는 Future<Done> 객체를 명시적으로 추출!
            Future<Done> future = executor.submit(() -> {
                long sleepTime = (taskId == 4) ? 10000 : 1000;
                Thread.sleep(sleepTime);
                return new Done("Thread-" + taskId, System.currentTimeMillis() - start);
            });

            // Future와 시작 시간을 묶어서 리스트에 보관!
            taskList.add(new TaskInfo(taskId, start, future));
            futures.add(future);
        }

        // [Mission 1]
        // 모든 Future를 순회하면서, 아직 완료되지 않은(isDone == false) 작업이 몇 개인지
        // 1초마다 체크하여 출력하는 로직을 작성해 보세요.
        // (힌트: while 루프와 Thread.sleep(1000)을 활용)

        while (/* 모든 작업이 완료될 때까지 */ true) {
            long incompleteCount = futures.stream().filter(f -> !f.isDone()).count();
            System.out.println("[%s] 현재 대기 중인 I/O 작업 수: %d".formatted(LocalTime.now(), incompleteCount));

            if (incompleteCount == 0) break;
            Thread.sleep(1000);

            // [Mission 2]
            // 만약 특정 작업이 3초 이상 걸린다면 "시스템 병목 감지!"라는 메시지를 출력해 보세요.
            long now = System.currentTimeMillis();

            for (TaskInfo task : taskList) {
                if (!task.future().isDone()) {
                    if (now - task.startTime() > 3000) {
                        System.out.printf("[%s] [경고] %d번 작업 병목 감지! (경과: %dms)%n",
                                LocalTime.now(), task.id(), now - task.startTime());
                    }
                }
            }
        }

        executor.shutdown();
        System.out.println("[%s] 모든 로그 수집 완료".formatted(LocalTime.now()));
    }
}
