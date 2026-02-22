package example.Predicate;

import java.util.*;
import java.util.stream.Collectors;

public class CodeKata20260209 {

    public record LogEntry(
            Long id,
            String serviceName, // "AUTH", "ORDER", "PAYMENT"
            String level,       // "INFO", "WARN", "ERROR"
            String message,
            long responseTimeMs // 응답 시간 (ms)
    ) {}

    // [문제 1] "서비스별 평균 응답 시간" (groupingBy + averagingLong)
    public Map<String, Double> getAverageResponseTimeByService(List<LogEntry> logs) {
        Map<String, List<Long>> tempMap = new HashMap<>();
        for (LogEntry log : logs) {
            if (log != null) {
                tempMap.computeIfAbsent(log.serviceName(), k -> new ArrayList<>())
                        .add(log.responseTimeMs());
            }
        }

        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : tempMap.entrySet()) {
            long sum = 0;
            for (long time : entry.getValue()) {
                sum += time;
            }
            result.put(entry.getKey(), (double) sum / entry.getValue().size());
        }
        return result;
    }
    // 결과 : 등급: A- (Excellent)
    // 엔트리 레코드는 long타입인데 averagingDouble를 사용함. averagingLong을 쓰는 것이 더 효과적임.
    // [사소한 개선점: Null Safety] LogEntry::serviceName이 null일 경우, 결과 맵의 키(Key)로 null이 들어갈 수 있습니다.
    // (Day 2에서 다뤘던 groupingBy의 특성 기억하시죠?) 운영 대시보드에 "Service: null, Avg: 120ms"라고 뜨면 이상하니까요.
    public Map<String, Double> getAverageResponseTimeByServiceTobe(List<LogEntry> logs) {
        return logs.stream()
                .filter(Objects::nonNull)
                .filter(log -> log.serviceName() != null) // 안전장치 추가
                .collect(Collectors.groupingBy(LogEntry::serviceName,
                        Collectors.averagingDouble(LogEntry::responseTimeMs)));
    }

    // [문제 2] "에러 로그 요약 메시지" (joining)
    public String getErrorSummary(List<LogEntry> logs) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (LogEntry log : logs) {
            if (log != null && "ERROR".equals(log.level())) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(log.message());
                first = false;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // 결과 등급: B+ (Functional Mismatch)
    // 총평 : Collectors.joining의 개념과 인자 순서(delimiter, prefix, suffix)는 완벽합니다.
    // 하지만, **"요구사항의 디테일(Detail)"**을 하나 놓쳤습니다.
    public String getErrorSummaryTobe(List<LogEntry> logs) {
        return logs.stream()
                .filter(Objects::nonNull)
                .filter(log -> "ERROR".equals(log.level()))
                .map(LogEntry::message)
                .collect(Collectors.joining(",", "[", "]"));
    }

    /* 2번 문제 A+ 코드
    public String getErrorSummaryTobe(List<LogEntry> logs) {
        return logs.stream()
                .filter(Objects::nonNull)
                // 1. 대소문자 무시 (안전성 강화)
                .filter(log -> "ERROR".equalsIgnoreCase(log.level()))
                // 2. 메시지가 null일 경우 방어 (빈 문자열 처리)
                .map(log -> Objects.requireNonNullElse(log.message(), ""))
                // 3. 구분자에 공백 추가 (", ")
                .collect(Collectors.joining(", ", "[", "]"));
    }
     */

    // [문제 3] "통계 요약 리포트" (summarizingLong)
    public LongSummaryStatistics getResponseTimeStats(List<LogEntry> logs) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0;
        long count = 0;

        for (LogEntry log : logs) {
            if (log != null) {
                long time = log.responseTimeMs();
                if (time < min) min = time;
                if (time > max) max = time;
                sum += time;
                count++;
            }
        }
        // Statistics 객체를 수동으로 생성하는 번거로움...
        LongSummaryStatistics stats = new LongSummaryStatistics();
        if (count > 0) {
            stats.accept(min); // 실제론 이렇게 안 쓰지만 레거시라면..
            stats.accept(max);
            // (대충 수동으로 데이터 세팅하는 복잡한 과정)
        }
        return stats;
    }

    // LongSummaryStatistics 클래스와 Collectors.summarizingLong 메서드의 존재를 아는 것만으로도 상위 10%입니다.
    public LongSummaryStatistics getResponseTimeStatsTobe(List<LogEntry> logs) {
        return logs.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.summarizingLong(LogEntry::responseTimeMs));
    }


}
