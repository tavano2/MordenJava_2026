package example.Part2;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectorBasicExperiment {

    public static void main(String[] args) {
        // 데이터 준비: 중복이 포함된 서버 로그 레벨
        List<String> rawLogs = List.of("INFO", "ERROR", "DEBUG", "ERROR", "WARN", "INFO");

        System.out.println("=== Experiment 1: Collectors.toList() (Traditional) ===");
        // 1. 기존 방식: Collectors.toList() 사용
        List<String> mutableList = rawLogs.stream()
                .filter(log -> log.length() > 3) // 3글자 초과만 필터링
                .collect(Collectors.toList());

        printDetails("Mutable List", mutableList);

        // ⚠️ 실험: 수집된 리스트에 새로운 요소를 추가할 수 있을까?
        try {
            mutableList.add("FATAL"); // 가변(Mutable) 리스트라 성공함
            System.out.println("👉 결과: 요소 추가 성공! (List size: " + mutableList.size() + ")");
        } catch (UnsupportedOperationException e) {
            System.out.println("👉 결과: 요소 추가 실패 (불변 리스트)");
        }


        System.out.println("\n=== Experiment 2: Stream.toList() (Java 16+) ===");
        // 2. Modern 방식: Stream.toList() 사용
        List<String> immutableList = rawLogs.stream()
                .filter(log -> log.length() > 3)
                .toList(); // ✨ 코드가 훨씬 간결함

        printDetails("Immutable List", immutableList);

        // ⚠️ 실험: 이 리스트에도 요소를 추가할 수 있을까?
        try {
            immutableList.add("FATAL"); // 불변(Immutable) 리스트라 예외 발생
            System.out.println("👉 결과: 요소 추가 성공!");
        } catch (UnsupportedOperationException e) {
            System.out.println("👉 결과: 💥 예외 발생! 요소 추가 실패 (불변 리스트)");
            System.out.println("   (이것이 Modern Java가 지향하는 데이터 무결성입니다)");
        }


        System.out.println("\n=== Experiment 3: Collectors.toSet() (Unordered) ===");
        // 3. Set으로 수집 (중복 제거)
        Set<String> logSet = rawLogs.stream()
                .collect(Collectors.toSet());

        // Set은 순서를 보장하지 않음 (실행할 때마다 순서가 다를 수 있음)
        printDetails("Log Set", logSet);
    }

    // 결과물의 실제 클래스 타입과 내용을 출력하는 헬퍼 메서드
    private static void printDetails(String label, Object collection) {
        System.out.println("[" + label + "]");
        System.out.println("   Type: " + collection.getClass().getName()); // 내부 구현체 확인
        System.out.println("   Data: " + collection);
    }
}
