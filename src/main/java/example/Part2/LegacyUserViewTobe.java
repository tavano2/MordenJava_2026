package example.Part2;
import java.util.Optional;

public class LegacyUserViewTobe {
    record City(String name) {}
    record Address(City city, String street) {}
    record User(String name, Address address) {}

    public static void main(String[] args) {
        User user1 = new User("Dev", new Address(new City("Seoul"), "Gangnam"));
        User user2 = new User("Junior", new Address(null, "Bundang")); // City is null
        User user3 = new User("Ghost", null); // Address is null
        User user4 = null; // User is null

        System.out.println(getUserCityName(user1)); // Seoul
        System.out.println(getUserCityName(user2)); // Unknown
        System.out.println(getUserCityName(user3)); // Unknown
        System.out.println(getUserCityName(user4)); // Unknown
    }

    public static String getUserCityName(User user) {

        // flatMap을 사용 할 수 있으나 코드가 길어짐
//        Optional.ofNullable(user)
//                .flatMap(u -> Optional.ofNullable(u.address())) // 중요: 직접 Optional로 감싸야 함!
//                .flatMap(a -> Optional.ofNullable(a.city()))    // 중요: 직접 Optional로 감싸야 함!
//                .map(City::name)                             // 마지막은 String이니 그냥 map 사용
//                .orElse("Unknown");

        // 일반적인 map 사용시
        return Optional.ofNullable(user)      // 1. null일 수도 있는 user를 박스에 포장
                .map(User::address)           // 2. 박스 안의 user가 있으면 address를 꺼냄 (null이면 빈 박스 됨)
                .map(Address::city)           // 3. address가 있으면 city를 꺼냄
                .map(City::name)              // 4. city가 있으면 name을 꺼냄
                .orElse("Unknown");           // 5. 중간에 하나라도 없어서 빈 박스가 되었다면 "Unknown" 반환


    }
}
