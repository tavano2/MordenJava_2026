package example.Part2;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class SearchDeepDive {

    // Java 14 Record: 데이터를 담는 불변 객체 (Boilerplate 제거)
    record User(int id, String name, boolean isVip) {}

    public static void main(String[] args) {
        // 1. 데이터 준비 (1부터 100까지의 User)
        List<User> users = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new User(i, "User" + i, i % 10 == 0)) // 10번째마다 VIP
                .toList(); // Java 16: Stream.toList()

        System.out.println("--- [Experiment 1] findFirst vs findAny (Parallel) ---");

        // 시나리오: VIP 유저를 찾아라 (병렬 처리)
        // 병렬 스트림은 여러 스레드가 동시에 작업하므로 순서가 보장되지 않는 환경입니다.

        System.out.println("\n1. findFirst() in Parallel:");
        Optional<User> firstVip = users.parallelStream()
                .filter(u -> {
                    printThreadInfo("Filter checking " + u.id());
                    return u.isVip();
                })
                .findFirst();

        System.out.println("Result: " + firstVip.orElseThrow());
        // 예상: 병렬이라 스레드는 뒤죽박죽이어도, 결과는 무조건 ID가 가장 낮은 VIP(10)여야 함.


        System.out.println("\n2. findAny() in Parallel:");
        Optional<User> anyVip = users.parallelStream()
                .filter(u -> {
                    printThreadInfo("Filter checking " + u.id());
                    return u.isVip();
                })
                .findAny();

        System.out.println("Result: " + anyVip.orElseThrow());
        // 예상: 실행할 때마다 결과가 달라질 수 있음 (10, 20, 30... 중 먼저 찾은 놈)


        System.out.println("\n--- [Experiment 2] Short-circuiting (anyMatch) ---");

        // 시나리오: ID가 50인 유저가 존재하는지 확인 (순차 스트림)
        boolean exists = users.stream()
                .peek(u -> System.out.println("Peek: " + u.id())) // 방문 로그
                .anyMatch(u -> u.id() == 5);

        System.out.println("Exists? " + exists);
        // 예상: 1~5까지만 출력되고 6부터는 출력되지 않아야 함 (Short-circuit)
    }

    // 현재 실행 중인 스레드 정보를 출력하는 헬퍼 메서드
    private static void printThreadInfo(String msg) {
        // 병렬 처리를 눈으로 확인하기 위해 스레드 이름 출력
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }
}
