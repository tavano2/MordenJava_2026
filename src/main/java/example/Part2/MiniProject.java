package example.Part2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiniProject {
    public record ServerLog(String servername, List<LogFile> files) {}
    public record LogFile (String filename, List<String> content) {}
    public record LogEntry(String timestamp, String logLevel, String serviceName, String message) {}

    // Helper: 테스트 데이터 생성
    public static List<ServerLog> getMockData() {
        LogFile file1 = new LogFile("serverA_1.log", List.of(
                "2024-05-20 10:00:00,INFO,OrderService,Order created",
                "2024-05-20 10:02:00,ERROR,PaymentService,Payment failed",
                "2024-05-20 10:05:00,INFO,DeliveryService,Shipped"
        ));

        LogFile file2 = new LogFile("serverA_2.log", List.of(
                "2024-05-20 11:00:00,WARN,OrderService,Stock low",
                "2024-05-20 11:05:00,ERROR,OrderService,DB Connection timeout"
        ));

        LogFile file3 = new LogFile("serverB_1.log", List.of(
                "2024-05-20 12:00:00,INFO,PaymentService,Refund processed",
                "2024-05-20 12:10:00,ERROR,PaymentService,Gateway error"
        ));

        return List.of(
                new ServerLog("Server A", List.of(file1, file2)),
                new ServerLog("Server B", List.of(file3))
        );
    }

    // [미션] 모든 서버의 로그를 하나의 스트림으로 평탄화하세요.
    public static Stream<LogEntry> flattenLogs(List<ServerLog> servers) {
        return servers.stream()
        // 1. ServerLog -> LogFile 스트림으로 변환 (flatMap)
                .flatMap(serverLog -> serverLog.files().stream())
        // 2. LogFile -> String(Line) 스트림으로 변환 (flatMap)
                .flatMap(logFile -> logFile.content().stream())
        // 3. String -> LogEntry 객체로 변환 (map)
                .map(s -> {
                    String[] content = s.split(",");
                    return new LogEntry(content[0], content[1], content[2], content[3]);
                });
        // Hint: split(",")을 사용하고, 배열 인덱스로 LogEntry 생성
    }

    public static Map<String, Long> getErrorCountPerService(Stream<LogEntry> logStream) {
        // 여기에 코드를 작성해 주세요.
        return logStream.filter(el -> "ERROR".equals(el.logLevel()))
                .collect(Collectors.groupingBy(LogEntry::serviceName, Collectors.counting()));
    }

    public static void main(String[] args) {
        // 실습 테스트 시작
        /*
        List<ServerLog> testData = getMockData();
        Stream<LogEntry> testEntry = flattenLogs(testData);
        testEntry.forEach(logEntry -> {
            System.out.println("timestamp :: " + logEntry.timestamp());
            System.out.println("logLevel :: " +logEntry.logLevel());
            System.out.println("serviceName :: " +logEntry.serviceName());
            System.out.println("message :: " +logEntry.message());
        });
        Map<String, Long> testCount = getErrorCountPerService(testEntry);
        testCount.forEach((s, cnt) -> {
            System.out.println("Service Name :: " + s);
            System.out.println("count :: " + cnt);
        });
         */
        // 실습 테스트 종료 아래는 맨 마지각 실습 코드
        // 1. 데이터 준비
        List<ServerLog> rawData = getMockData();
        // 2. 파이프라인 연결 및 분석 (Stream은 한 번 쓰면 닫히므로 주의!)
        // flattenLogs를 통해 얻은 스트림을 바로 분석 메서드에 넘겨보세요.
        Map<String, Long> errorStats = getErrorCountPerService(flattenLogs(rawData));
        // 3. 결과 정렬 및 출력 (내림차순)
        // TODO: errorStats를 스트림으로 열고, value 기준으로 내림차순 정렬하여 출력하세요.
        errorStats.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(
                        stringLongEntry -> {
                            System.out.println("[순위] " + stringLongEntry.getKey()  + ":" + stringLongEntry.getValue() );
                        }
                );
        System.out.println("=== 📊 Enterprise Error Report ===");

    }


}
