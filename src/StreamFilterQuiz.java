import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate; // 이걸 import 해야 Predicate를 쓸 수 있어요!

public class StreamFilterQuiz {

    public static void main(String[] args) {
        // 1. 테스트 데이터 세팅
        List<Customer> customers = Arrays.asList(
                new Customer("철수", false, Tier.VIP, 5),      // 대상 O (VIP)
                new Customer("영희", false, Tier.NORMAL, 15),  // 대상 O (구매 10회 이상)
                new Customer("민수", true, Tier.VIP, 100),     // 대상 X (블랙리스트)
                new Customer("길동", false, Tier.NORMAL, 3)    // 대상 X (조건 미달)
        );

        // ==========================================
        // 2. 퀴즈: 아래 레거시 코드를 Modern Stream으로 바꿔보세요!
        // ==========================================

        // [Legacy Code]
        /*
        List<String> result = new ArrayList<>();
        for (Customer c : customers) {
            if (!c.isBlacklist()) { // 블랙리스트가 아니고
                // VIP거나 구매횟수 10회 이상
                if (c.getTier() == Tier.VIP || c.getPurchaseCount() >= 10) {
                    result.add(c.getName());
                }
            }
        }
        */

        // [Modern Code] - 여기에 작성해주세요!
        // List<String> result = customers.stream()
        //      ...
        Predicate<Customer> isBlacklist = Customer::isBlacklist;
        Predicate<Customer> isVip = customer -> customer.getTier() == Tier.VIP;
        Predicate<Customer> isLoyal = customer -> customer.getPurchaseCount() >= 10;

        List<String> result = customers.stream()
                .filter(Predicate.not(isBlacklist))
                .filter(isVip.or(isLoyal))
                .map(Customer::getName)
                .toList();


        // 3. 결과 출력
        // System.out.println(result);


    }

    // 1. DTO 클래스 (Main 클래스 내부에 static으로 추가하거나 별도로 정의)
    static class CustomerDto {
        private String name;
        private Tier tier;

        public CustomerDto(String name, Tier tier) {
            this.name = name;
            this.tier = tier;
        }

        // toString() for printing
        @Override
        public String toString() { return "DTO(" + name + ", " + tier + ")"; }
    }

    // 2. 퀴즈
    List<Customer> customers = Arrays.asList(
            new Customer("철수", false, Tier.VIP, 5),
            new Customer("영희", false, Tier.NORMAL, 15)
    );

    // [Legacy]
    /*
    List<CustomerDto> dtos = new ArrayList<>();
    for (Customer c : customers) {
        dtos.add(new CustomerDto(c.getName(), c.getTier()));
    }
    */

    // [Modern] - map을 사용하여 한 줄로 끝내보세요!
    // 힌트: 람다식 (c -> new ...)을 써도 되고,
    // 생성자 참조 (ClassName::new)를 쓰면 더 멋집니다!

    // List<CustomerDto> dtos = ...
    List<CustomerDto> dtos = customers.stream()
            .map(customer -> new CustomerDto(customer.getName(), customer.getTier()))
            .toList();


    // --- 실습용 DTO 및 Enum (건드리지 않으셔도 됩니다) ---

    enum Tier { VIP, NORMAL }

    static class Customer {
        private String name;
        private boolean blacklist;
        private Tier tier;
        private int purchaseCount;

        public Customer(String name, boolean blacklist, Tier tier, int purchaseCount) {
            this.name = name;
            this.blacklist = blacklist;
            this.tier = tier;
            this.purchaseCount = purchaseCount;
        }

        public String getName() { return name; }
        public boolean isBlacklist() { return blacklist; }
        public Tier getTier() { return tier; }
        public int getPurchaseCount() { return purchaseCount; }
    }
}