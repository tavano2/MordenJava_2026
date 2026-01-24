package Part2;
import java.util.*;
import java.util.stream.*;

public class SlicingLab {

    // 시간(hour)과 메시지를 담은 불변 레코드
    public record Log(int hour, String message) {}

    public static void main(String[] args) {
        // 이미 시간순으로 정렬된 데이터 (09시 ~ 18시)
        List<Log> dailyLogs = List.of(
                new Log(9, "출근"),
                new Log(10, "오전 회의"),
                new Log(11, "코드 리뷰"),
                new Log(14, "점심 복귀"), // Target 경계 (여기서 멈춰야 함)
                new Log(15, "개발 진행"),
                new Log(16, "배포 시작"),
                new Log(18, "퇴근")
        );

        System.out.println("--- [1] Java 8: filter (전수 조사) ---");
        // filter는 조건이 false가 나와도, "혹시 뒤에 true가 있을까 봐" 끝까지 갑니다.
        List<Log> morningLogs = dailyLogs.stream()
                .peek(log -> System.out.println("👀 Checking(Filter): " + log.hour()))
                .filter(log -> log.hour() < 12)
                .toList(); // Java 16+

        System.out.println("👉 결과 개수: " + morningLogs.size());


        System.out.println("\n--- [2] Java 9: takeWhile (Short-Circuit) ---");
        // takeWhile은 조건이 false가 되는 순간, "뒤는 볼 필요 없다"며 즉시 멈춥니다.
        // *전제조건: 데이터가 정렬되어 있어야 함*
        List<Log> morningLogsOptimized = dailyLogs.stream()
                .peek(log -> System.out.println("⚡ Checking(takeWhile): " + log.hour()))
                .takeWhile(log -> log.hour() < 12)
                .toList();

        System.out.println("👉 결과 개수: " + morningLogsOptimized.size());

        // 참고: 반대 개념인 dropWhile은 조건이 참인 동안은 '버리다가',
        // 처음 거짓이 되는 순간부터 '나머지 전부'를 가져옵니다. (ex: 오후 로그만 조회)
    }
}