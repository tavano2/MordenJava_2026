package example.Predicate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class CodeKata20260218 {

    // [문제 1] "병렬 스트림의 함정: 공유 자원 오염"
    // 미션: 외부 변수(sum[0])에 의존하는 Side-effect를 제거하고,
    // 스트림 자체의 리덕션(Reduction) 연산을 사용하여 스레드 안전하게 리팩토링하세요.
    public long sumParallel(int range) {
        long[] sum = {0}; // 공유 자원
        IntStream.rangeClosed(1, range)
                .parallel()
                .forEach(i -> sum[0] += i); // Race Condition 발생
        return sum[0];
    }

    // S (Perfect Execution)
    // 외부 상태(공유 변수 sum[0])에 의존하던 부수 효과(Side-effect)를 완벽하게 제거하고,
    // 리덕션(Reduction) 연산인 .reduce를 사용하여 함수형 프로그래밍의 원칙을 지켰습니다.
    public static long sumParallelTobe(int range) {
        return IntStream.rangeClosed(1, range)
                .parallel()
                .reduce(0, Integer::sum);
    }

    // [문제 2] "병렬화가 오히려 독이 되는 경우"
    // 미션: LinkedList는 병렬 처리를 위해 데이터를 쪼개는(Splitting) 비용이 매우 큽니다.
    // 이 코드를 병렬 효율이 가장 좋은 데이터 구조로 변경하여 리팩토링하거나, 왜 병렬 처리가 부적절한지 주석으로 설명해 보세요.
    public List<String> processParallel(LinkedList<String> data) {

        return data.parallelStream() // 데이터 소스가 LinkedList?
                .filter(s -> s.length() > 5)
                .map(String::toUpperCase)
                .toList();
    }

    // 2번 해답
    // LinkedList: 메모리에 흩어져(Scattered) 있고, 포인터(주소)로 연결되어 있습니다.
    // ArrayList: 메모리에 **연속적(Contiguous)**으로 할당되어 있습니다.
    // 따라서, 구조를 ArrayList로 변경한 후 스트림을 태우는 것이 정답입니다. 혹은 위 리스트를 일반 stream으로 변경하는게 좋습니다.



    // [Mock Method] 오래 걸리는 네트워크 작업을 흉내 냄
    private String downloadContent(String url) {
        System.out.println("Downloading " + url + " on [" + Thread.currentThread().getName() + "]");
        try {
            // 2초간 멈춤 (Blocking I/O Simulation)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "Content of " + url;
    }

    // 공용 풀을 오염시키지 않도록 **커스텀 ForkJoinPool**을 생성하여
    // 병렬 작업을 격리시키는 코드로 리팩토링하세요. (8년 차 시니어라면 운영 환경의 안정성을 위해 꼭 알아야 할 패턴입니다.)
    public void heavyIoTask(List<String> urls) {
        urls.parallelStream()
                .map(this::downloadContent) // 블로킹 I/O 발생!
                .forEach(System.out::println);
    }

    private String downloadContentTobe(String url) {
        System.out.println("Downloading " + url + " on [" + Thread.currentThread().getName() + "]");
        try {
            // 2초간 멈춤 (Blocking I/O Simulation)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "Content of " + url;
    }

    // 평가 : B+ get을 빼먹음)
    public void heavyIoTaskTobe(List<String> urls) {
        int treadCount = 4;

        try (ForkJoinPool customPool = new ForkJoinPool(treadCount);){
            customPool.submit(() -> {
                urls.parallelStream()
                        .map(this::downloadContentTobe) // 블로킹 I/O 발생!
                        .forEach(System.out::println);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void main() {
        long test = sumParallelTobe(5);
        System.out.println(test);
    }

}
