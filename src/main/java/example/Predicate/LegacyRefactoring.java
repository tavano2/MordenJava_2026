package example.Predicate;
import java.util.*;
import java.util.stream.Collectors;

public class LegacyRefactoring {

    // 1. 이 클래스를 record로 변경해 보세요.
    // (Lombok @Data가 적용된 상태라고 가정하고, 실행을 위해 Getter/Setter를 풀어서 썼습니다)
//    static class Server {
//        private String name;
//        private String region;
//        private Double cpuUsage;
//        private boolean isMaintenance;
//
//        public Server(String name, String region, Double cpuUsage, boolean isMaintenance) {
//            this.name = name;
//            this.region = region;
//            this.cpuUsage = cpuUsage;
//            this.isMaintenance = isMaintenance;
//        }
//
//        public String getName() { return name; }
//        public String getRegion() { return region; }
//        public Double getCpuUsage() { return cpuUsage; }
//        public boolean isMaintenance() { return isMaintenance; }
//
//        // Setter는 레거시 코드의 특징(가변성)을 보여주기 위해 남겨둠
//        public void setName(String name) { this.name = name; }
//    }

    record Server(String name, String region, Double cpuUsage, boolean isMaintenance) {};

    public static void main(String[] args) {
        // 테스트 데이터 생성
        List<Server> servers = Arrays.asList(
                new Server("web-01", "kr-1", 85.5, false),  // Target (O)
                new Server("web-02", "us-1", 90.0, false),  // Region mismatch (X)
                new Server("db-01", "kr-1", 40.0, false),   // Low CPU (X)
                new Server("batch-01", "kr-1", 99.9, true), // Maintenance (X)
                new Server(null, "kr-1", 88.0, false),      // Name is null (X)
                new Server("cache-01", null, 88.0, false),  // Region is null (X)
                null                                        // Server is null (X)
        );

        LegacyRefactoring app = new LegacyRefactoring();

        System.out.println("=== Legacy Result ===");
        // List<String> legacyResult = app.getHighLoadServerNames(servers);
        // System.out.println(legacyResult); // 예상 결과: [WEB-01]

        System.out.println("\n=== Refactored Result ===");
        // TODO: 리팩토링한 메서드를 호출해서 결과가 같은지 확인하세요.
        List<String> modernResult = app.getHighLoadServerNamesModern(servers);
        System.out.println(modernResult);
    }

    // ▼▼▼ 리팩토링 대상 메서드 ▼▼▼
    /*
    public List<String> getHighLoadServerNames(List<Server> servers) {
        List<String> result = new ArrayList<>();

        for (Server server : servers) {
            // 1. null 체크 지옥
            if (server != null) {
                // 2. 유지보수 중이 아닌 것
                if (!server.isMaintenance()) {
                    // 3. Region이 kr-1 인 것 (null safe)
                    if (server.getRegion() != null && server.getRegion().equals("kr-1")) {
                        Double cpu = server.getCpuUsage();
                        // 4. CPU 사용량이 80 이상인 것
                        if (cpu != null && cpu >= 80.0) {
                            String name = server.getName();
                            // 5. 이름이 있으면 대문자로 변환
                            if (name != null) {
                                result.add(name.toUpperCase());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

     */

    // TODO: 여기에 getHighLoadServerNamesModern 메서드를 작성하세요.
    // 결과 : B+
//    public List<String> getHighLoadServerNamesModern(List<Server> servers) {
//        return servers.stream()
//                .filter(Objects::nonNull)
//                .filter(s -> !s.isMaintenance())
//                .filter(s-> s.region() != null && "kr-1".equals(s.region()))
//                .filter(s -> s.cpuUsage() != null && s.cpuUsage() >= 80.0)
//                .filter(s -> s.name() != null)
//                .map(s-> s.name().toUpperCase())
//                .toList();
//    }

    // A+ 예제
    // 상수 추출 (유지보수의 기본)
    private static final String TARGET_REGION = "kr-1";
    private static final double HIGH_LOAD_THRESHOLD = 80.0;

    public List<String> getHighLoadServerNamesModern(List<Server> servers) {
        return servers.stream()
                .filter(Objects::nonNull) // 1. 깡통 객체 제거
                .filter(s -> !s.isMaintenance()) // 2. 유지보수 제외
                .filter(s -> TARGET_REGION.equals(s.region())) // 3. Null safe equals (간결함!)
                .filter(s -> isHighLoad(s.cpuUsage())) // 4. 로직 분리 (가독성 UP)
                .map(Server::name) // 5. 이름 추출
                .filter(Objects::nonNull) // 6. 이름 없는 놈 제거
                .map(String::toUpperCase) // 7. 대문자 변환
                .toList(); // Java 16+
    }

    // 복잡한 조건(null 체크 + 부등호)은 별도 메서드로 빼는 게 "매너"입니다.
    private boolean isHighLoad(Double cpuUsage) {
        return cpuUsage != null && cpuUsage >= HIGH_LOAD_THRESHOLD;
    }

    // 2단계 map 변환
    public Map<String, Long> countServersByRegion(List<Server> servers) {
        Map<String, Long> result = new HashMap<>();
        for (Server server : servers) {
            if (server != null && server.region() != null) {
                String region = server.region();
                if (result.containsKey(region)) {
                    result.put(region, result.get(region) + 1);
                } else {
                    result.put(region, 1L);
                }
            }
        }
        return result;
    }

    // 결과 : 등급: A- (Excellent, but missed one edge case)
    public Map<String, Long> countServersByRegionTobe(List<Server> servers) {
        return servers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Server::region, Collectors.counting()));
    }

    // A+ 예제
//    public Map<String, Long> countServersByRegionTobe(List<Server> servers) {
//        return servers.stream()
//                .filter(Objects::nonNull)
//                .filter(s -> s.region() != null) // <--- 이 한 줄이 "디테일"입니다.
//                .collect(Collectors.groupingBy(Server::region, Collectors.counting()));
//    }

    // 3번 과제


}
