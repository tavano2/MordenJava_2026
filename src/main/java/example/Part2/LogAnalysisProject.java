package example.Part2;
import java.util.*;
import java.util.stream.Collectors;

public class LogAnalysisProject {

    // 로그 데이터 구조 (서비스명, 로그레벨, 응답시간)
    record LogEntry(String serviceName, String logLevel, int responseTime) {
        public static LogEntry parse(String rawLog) {
            String[] parts = rawLog.split(",");
            return new LogEntry(parts[0], parts[1], Integer.parseInt(parts[2]));
        }
    }

    public static void main(String[] args) {
        // 원본 로그 데이터 (실제로는 파일이나 DB에서 읽어옴)
        List<String> rawLogs = List.of(
                "OrderService,INFO,20",
                "OrderService,ERROR,500", // 🚨 대상
                "PaymentService,INFO,15",
                "PaymentService,ERROR,2000", // 🚨 대상
                "PaymentService,ERROR,1500", // 🚨 대상
                "DeliveryService,INFO,50",
                "OrderService,WARN,100"
        );

        LogAnalyzer analyzer = new LogAnalyzer();
        Map<String, List<LogEntry>> errorReport = analyzer.analyzeErrorLogs(rawLogs);
        Map<String, Double> perReport = analyzer.calculateAverageResponseTime(rawLogs);
        Map<String, Optional<LogEntry>> findLogLog = analyzer.findSlowestLogPerService(rawLogs);

        // 결과 검증
        errorReport.forEach((service, logs) -> {
            System.out.println("Service: " + service + " | Error Count: " + logs.size());
            logs.forEach(log -> System.out.println("   - " + log));
        });

        perReport.forEach((s, aDouble) -> {
            System.out.println("Service: " + s + " | Performance: " + aDouble);
        });

        findLogLog.forEach((s, logEntry) -> {
            System.out.println("Service: " + s + " | Log Log: " + logEntry);
        });
    }

    static class LogAnalyzer {

        // [미션]
        // 1. rawLogs("서비스명,레벨,시간") 문자열을 파싱하여 LogEntry 객체로 변환하세요. (map)
        // 2. "ERROR" 레벨인 로그만 필터링하세요. (filter)
        // 3. 서비스명(serviceName) 별로 그룹핑하여 Map을 반환하세요. (groupingBy)
        public Map<String, List<LogEntry>> analyzeErrorLogs(List<String> rawLogs) {

            return rawLogs.stream()
                    // TODO: 이곳을 Stream API로 채워주세요.
                    // Hint: 문자열 분해는 log.split(",")을 사용하세요.
//                    .map(s -> {
//                        String [] splitString = s.split(",");
//                        return new LogEntry(splitString[0], splitString[1], Integer.parseInt(splitString[2]));
//                    })
                    .map(LogEntry::parse) // 레코드에 파싱 기능을 추가한다면 직관적으로 변신
                    .filter(e -> "ERROR".equals(e.logLevel()))
                    .collect(Collectors.groupingBy(LogEntry::serviceName));
        }

        // 예시 입력: "OrderService,INFO,100", "OrderService,INFO,200"
        // 예시 출력: OrderService -> 150.0
        public Map<String, Double> calculateAverageResponseTime(List<String> rawLogs) {
            // 코드를 작성해 보세요.
            return rawLogs.stream()
                    .map(LogEntry::parse)
                    .filter(e -> "INFO".equals(e.logLevel()))
                    .collect(
                            Collectors.groupingBy(
                                    LogEntry::serviceName,
                                    Collectors.averagingDouble(LogEntry::responseTime
                                    )
                            )
                    );
        }

        // 각 서비스마다 가장 느렸던(응답 시간이 가장 긴) 로그
        // 필터링 없이 모든 로그를 대상으로 합니다.
        // 결과는 Map<String, Optional<LogEntry>> 형태가 됩니다.
        public Map<String, Optional<LogEntry>> findSlowestLogPerService(List<String> rawLogs) {
            // 코드를 작성해 보세요.
            return rawLogs.stream()
                    .map(LogEntry::parse)
                    .collect(Collectors.groupingBy(LogEntry::serviceName, Collectors.maxBy(
                            Comparator.comparingInt(LogEntry::responseTime)
                    )));
        }
    }
}