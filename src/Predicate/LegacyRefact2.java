package Predicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LegacyRefact2 {

    // 1. Record에 tags 필드 추가
    record Server(String name, List<String> tags) {}

    // 2. Legacy Code (이걸 리팩토링 하세요)
    public Map<String, Long> countAllTags(List<Server> servers) {
        Map<String, Long> tagCounts = new HashMap<>();
        for (Server server : servers) {
            if (server != null && server.tags() != null) {
                for (String tag : server.tags()) { // 이중 루프 발생!
                    tagCounts.put(tag, tagCounts.getOrDefault(tag, 0L) + 1);
                }
            }
        }
        return tagCounts;
    }

    // 등급: B
    public Map<String, Long> countAllTagsTobe(List<Server> servers) {
        return servers.stream()
                .filter(Objects::nonNull)
                .flatMap(s -> s.tags().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }

    // A+ 코드
//    public Map<String, Long> countAllTagsTobe(List<Server> servers) {
//        return servers.stream()
//                .filter(Objects::nonNull)
//                .filter(s -> s.tags() != null) // <--- [필수] 방어 코드 추가 해당 코드를 추가해야 tag list npe가 발생하지 않음
//                .flatMap(s -> s.tags().stream())
//                .filter(Objects::nonNull)      // 태그 내용물 중 null 제거 ("web", null, "db")
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//    }

}
