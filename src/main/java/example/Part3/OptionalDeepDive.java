package example.Part3;

import java.util.Optional;

public class OptionalDeepDive {

    // --- 1. Domain Model (Java 14+ Record) ---
    // 데이터 운반체(Data Carrier)는 불변이어야 하므로 Record가 제격이다.
    // 실무 Tip: 필드는 Nullable하게 두고, 접근자(Accessor)에서 Optional을 반환하는 패턴.

    record Address(String city, String street) {
        // city는 필수값이 아닐 수 있다고 가정
        public Optional<String> getCity() {
            return Optional.ofNullable(city);
        }
    }

    record User(String name, Address address) {
        // User는 Address가 없을 수도 있다.
        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }
    }

    record Order(Long id, User user) {
        // Order는 User 정보가 누락될 수도 있다 (예: 비회원 주문)
        public Optional<User> getUser() {
            return Optional.ofNullable(user);
        }
    }

    // --- 2. Logic Comparison ---

    /**
     * [Legacy Style]
     * 전형적인 방어적 코딩(Defensive Coding).
     * 비즈니스 로직보다 null 체크가 더 많아 가독성이 떨어진다.
     */
    public static String getCityNameLegacy(Order order) {
        if (order != null) {
            User user = order.user(); // 레코드의 기본 접근자 사용 (raw data)
            if (user != null) {
                Address address = user.address();
                if (address != null) {
                    String city = address.city();
                    if (city != null) {
                        return city.toUpperCase();
                    }
                }
            }
        }
        return "UNKNOWN";
    }

    /**
     * [Modern Style]
     * Optional Chaining을 이용한 선언형 프로그래밍.
     * "값이 있다면(ifPresent) ~하고, 아니면 말고"의 흐름.
     */
    public static String getCityNameModern(Order order) {
        return Optional.ofNullable(order)
                // 1. Order -> Optional<User> 반환하므로 flatMap 사용
                //    (여기서 map을 쓰면 Optional<Optional<User>>가 되어버림!)
                .flatMap(Order::getUser)

                // 2. User -> Optional<Address> 반환하므로 flatMap 사용
                .flatMap(User::getAddress)

                // 3. Address -> Optional<String> 반환하므로 flatMap 사용
                .flatMap(Address::getCity)

                // 4. String -> String (값 변환)이므로 map 사용
                .map(String::toUpperCase)

                // 5. 최종적으로 값이 없으면 기본값 제공
                // 중요: orElse(...)는 항상 실행됨(상수일 때 추천).
                //       orElseGet(() -> ...)은 필요할 때만 실행됨(메서드 호출일 때 추천).
                .orElse("UNKNOWN");
    }

    public static void main(String[] args) {
        // Case 1: 모든 데이터가 꽉 찬 경우 (Full Data)
        Address fullAddress = new Address("Seoul", "Gangnam-daero");
        User fullUser = new User("DevKim", fullAddress);
        Order fullOrder = new Order(1L, fullUser);

        // Case 2: 중간에 데이터가 빈 경우 (Broken Chain)
        // 주소는 있는데 city가 null인 상황
        Address missingCityAddress = new Address(null, "Teheran-ro");
        User userWithNoCity = new User("DevLee", missingCityAddress);
        Order brokenOrder = new Order(2L, userWithNoCity);

        // Case 3: 아예 Order가 null인 경우
        Order nullOrder = null;

        System.out.println("--- Legacy Check ---");
        System.out.println("Full: " + getCityNameLegacy(fullOrder));
        System.out.println("Broken: " + getCityNameLegacy(brokenOrder));
        System.out.println("Null: " + getCityNameLegacy(nullOrder));

        System.out.println("\n--- Modern Check ---");
        System.out.println("Full: " + getCityNameModern(fullOrder));
        System.out.println("Broken: " + getCityNameModern(brokenOrder));
        System.out.println("Null: " + getCityNameModern(nullOrder));
    }
}
