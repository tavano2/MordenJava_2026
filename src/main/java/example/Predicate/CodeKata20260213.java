package example.Predicate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodeKata20260213 {

    // 데이터 모델 (Record 활용)
    public record Settings(boolean isEmailEnabled) {}
    public record Profile(Settings settings) {}
    public record User(Profile profile) {}

    // "깊은 Null 체크 탈출" (Optional Chain)
//    public boolean isEmailEnabled(User user) {
//        if (user != null) {
//            Profile profile = user.getProfile();
//            if (profile != null) {
//                Settings settings = profile.getSettings();
//                if (settings != null) {
//                    return settings.isEmailEnabled();
//                }
//            }
//        }
//        return false;
//    }

    // 문제 1 "깊은 Null 체크 탈출" (Optional Chain)
    // S (Perfect Execution)
    // 훌륭합니다. **"Optional 체이닝의 정석"**을 보여주셨습니다.
    // 중첩된 if (user != null) 블록이 **철도 선로(Railway Oriented Programming)**처럼 매끄러운 단일 파이프라인으로 변했습니다.
    public boolean isEmailEnabledTobe(User user) {
        return Optional.ofNullable(user)
                .map(User::profile)
                .map(Profile::settings)
                .map(Settings::isEmailEnabled)
                .orElse(false);
    }

    public enum Status {
        PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERED, CANCELED
    }

    public record Order(Long id, Status status) {
    }


    // 목표: Map<Status, List<Long>> 만들기
//    public Map<Status, List<Long>> getOrderIdsByStatus(List<Order> orders) {
//        Map<Status, List<Long>> result = new HashMap<>();
//        for (Order order : orders) {
//            Status status = order.getStatus();
//            // ... (지저분한 초기화 로직 생략)
//            result.get(status).add(order.getId());
//        }
//        return result;
//    }

    // 문제 2 "데이터 그룹핑 및 통계" (Collectors.groupingBy)
    // 주문 리스트(List<Order>)를 받아 주문 상태(Status)별로 주문 ID들을 묶어야 합니다.
    // 결과 : S (Perfect Execution)
    // 총평
    // "이것이 시니어의 스트림이다." 라고 말하고 싶군요.
    // 대부분의 개발자가 groupingBy까지는 쓰지만, 그 뒤에 **Downstream Collector (mapping)**를 붙여서 한 번에 변환하는 기술은 잘 모릅니다.
    // 보통은 Map<Status, List<Order>>를 만든 뒤에 또 루프를 돌려서 ID를 꺼내죠.
    // 작성하신 코드는 **그룹핑과 데이터 변환(Extract ID)**을 단 한 번의 패스(Pass)로 끝냈습니다. 아주 효율적입니다.

    public Map<Status, List<Long>> getOrderIdsByStatusTobe(List<Order> orders) {
        return orders.stream()
                .filter(Objects::nonNull)
                .collect(Collectors
                        .groupingBy(Order::status, Collectors.mapping(Order::id, Collectors.toList())));
    }

    public record Employee(String name, List<String> skills) {}
    public record Department(String name, List<Employee> employees) {}

    public Set<String> getAllEmployeeSkills(List<Department> departments) {
        Set<String> allSkills = new HashSet<>();
        for (Department dept : departments) {
            for (Employee emp : dept.employees()) {
                allSkills.addAll(emp.skills()); // 2중 루프 + addAll
            }
        }
        return allSkills;
    }

    // 문제 3: "중첩 구조 평탄화 (flatMap)"
    // 결과 : A+ (Excellent with a Catch)
    // 총평
    /*
      노션 20260213 중첩 구조 평탄화 (flatMap) 참고
     */
    public Set<String> getAllEmployeeSkillsTobe(List<Department> departments) {
        return departments.stream()
                .filter(Objects::nonNull)
                .flatMap(dep -> dep.employees().stream())
                .filter(Objects::nonNull)
                .flatMap(emp -> emp.skills().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<String> getAllEmployeeSkillsTobeS(List<Department> departments) {
        return departments.stream()
                .filter(Objects::nonNull)
                // 리스트가 null이면 빈 스트림 반환
                .flatMap(dep -> dep.employees() == null ? Stream.empty() : dep.employees().stream())
                .filter(Objects::nonNull)
                .flatMap(emp -> emp.skills() == null ? Stream.empty() : emp.skills().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }



}
