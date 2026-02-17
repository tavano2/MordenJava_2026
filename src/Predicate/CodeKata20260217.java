package Predicate;

import java.util.*;
import java.util.stream.Collectors;

public class CodeKata20260217 {

    public enum DeliveryStatus {
        PREPARING, SHIPPED, COMPLETED, RETURNED
    }

    public record Address(String city, String street, String zipcode) {}
    public record Delivery(DeliveryStatus status, Address address) {}
    public record Order(Long id, Delivery delivery) {}

    // 문제 1 "중첩 Optional과 조건부 변환"
    // 목표: 배송 완료(COMPLETED) 상태일 때만 도시(City) 이름 반환
    // 미션: Optional의 체이닝(flatMap, filter, map)을 활용하여 if 문을 완전히 제거하세요.
    // 특히 배송 상태가 완료(COMPLETED)일 때만 주소를 가져오는 로직을 우아하게 녹여내는 것이 포인트입니다.
    public String getShippingCity(Order order) {
        if (order != null) {
            Delivery delivery = order.delivery(); // Record getter
            if (delivery != null && delivery.status() == DeliveryStatus.COMPLETED) {
                Address address = delivery.address();
                if (address != null) {
                    return address.city();
                }
            }
        }
        return "UNKNOWN";
    }

    // 등급: S (Perfect Execution)
    // **"군더더기 없는 Optional 체이닝의 정석"**입니다.
    // [Deep Dive]
    // map vs if ( != null): Order::delivery나 Delivery::address가 null을 반환하더라도, Optional.map 내부적으로 empty 상태로 전환되므로 NPE가 발생하지 않습니다. 아주 안전합니다.
    // orElse: 모든 체이닝 중 하나라도 실패하면 "UNKNOWN"을 반환하는 기본값 처리까지 완벽합니다.
    public String getShippingCityTobe(Order order) {
        return Optional.ofNullable(order)
                .map(Order::delivery)
                .filter(d -> d.status() == DeliveryStatus.COMPLETED)
                .map(Delivery::address)
                .map(Address::city)
                .orElse("UNKNOWN");

    }

    public record Employee(String name, String dept, String level, double salary) {}

    // 목표: 부서별 -> 직급별 -> 평균 급여 구하기
    // 미션: Collectors.groupingBy를 중첩(Nesting) 사용하고, Collectors.averagingDouble을 결합하여 단 한 줄의 스트림 문장으로 끝내보세요.
    // 결과 타입: Map<String, Map<String, Double>>
    public Map<String, Map<String, Double>> getAvgSalaryByDeptAndLevel(List<Employee> employees) {
        Map<String, Map<String, List<Employee>>> grouped = new HashMap<>();

        // 1단계: 그룹화 (수동)
        for (Employee e : employees) {
            grouped.computeIfAbsent(e.dept(), k -> new HashMap<>())
                    .computeIfAbsent(e.level(), k -> new ArrayList<>())
                    .add(e);
        }

        // 2단계: 평균 계산 (수동)
        Map<String, Map<String, Double>> result = new HashMap<>();
        for (String dept : grouped.keySet()) {
            Map<String, Double> levelAvgMap = new HashMap<>();
            for (String level : grouped.get(dept).keySet()) {
                List<Employee> empList = grouped.get(dept).get(level);
                double total = 0;
                for (Employee e : empList) total += e.salary();
                levelAvgMap.put(level, total / empList.size());
            }
            result.put(dept, levelAvgMap);
        }
        return result;
    }

    // 등급: S (Perfect Execution)
    // 중첩된 루프(Loop)와 computeIfAbsent로 떡칠되어 있던 코드가 단 한 줄의 스트림으로 정리가 되었습니다.
    // 계층 구조: dept -> level 순으로 그룹화가 이루어지는 과정이 눈에 보입니다.
    // 집계 함수: averagingDouble을 사용하여 최종 값을 Double로 변환했습니다. 아주 깔끔합니다.

    // 아래 코드는 2번째 소숫점까지 반올림 해달라고 할 때 collectingAndThen 메서드를 사용한 모습
    public Map<String, Map<String, Double>> getAvgSalaryByDeptAndLevelTobe(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy
                        (Employee::dept,
                                Collectors.groupingBy
                                        (Employee::level,
                                                Collectors.collectingAndThen(Collectors.averagingDouble(Employee::salary), d-> Math.round(d*100)/100.0))));
    }

    // "불변 객체(Record)와 Stream의 만남"
    public record Product(String name, int price, double discountRate) {}
    public record DiscountedProduct(String name, int discountedPrice) {
        static public int account(int price, double discountRate) {
            return (int) (price * (1.0 - discountRate));
        }
    }

    // 등급: B+ (Good Logic, but Imperative Style remains)
    // 로직은 정확합니다. record 안에 static 메서드를 만들어 계산 로직을 분리한 시도도 아주 훌륭합니다.
    // 하지만, **"스트림(Stream)을 흉내 낸 for-loop"**에 머물러 있습니다.

    // 상세한 내용은 20260217 불변 객체(Record)와 Stream의 만남 참고
    static void main() {
        List<Product> products = new ArrayList<>();
        List<DiscountedProduct> results = new ArrayList<>();
        products.forEach(p -> {
            if(p.price() >= 10000) {
                results.add(new DiscountedProduct(p.name(), DiscountedProduct.account(p.price(), p.discountRate())));
            }
        });
    }


    public record DiscountedProductTobeS(String name, int discountedPrice) {
        // 1. 계산 로직을 포함한 '정적 팩토리 메서드'
        // 'account'보다는 'from'이나 'of'가 관례입니다.
        public static DiscountedProductTobeS from(Product product) {
            int newPrice = (int) (product.price() * (1.0 - product.discountRate()));
            return new DiscountedProductTobeS(product.name(), newPrice);
        }
    }

    public List<DiscountedProductTobeS> getDiscountedProductsTobeS(List<Product> products) {
        return products.stream()
                // 1. 필터링: 비즈니스 로직(10,000원 이상)
                .filter(p -> p.price() >= 10000)
                // 2. 변환: Product -> DiscountedProduct (메서드 레퍼런스 활용!)
                .map(DiscountedProductTobeS::from)
                // 3. 수집: 불변 리스트로 반환 (Java 16+)
                .toList();
    }



}
