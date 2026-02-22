package example.Part2;
import java.util.Optional;

public class InsuranceSystem {

    // [중요] Getter가 이미 Optional을 반환하도록 설계됨 (Modern Style)
    record Insurance(String companyName) {}

    record Car(Optional<Insurance> insurance) {}

    record Person(Optional<Car> car) {}

    public static void main(String[] args) {
        // 1. 풀옵션 유저 (차도 있고 보험도 있음)
        Person richPerson = new Person(
                Optional.of(new Car(
                        Optional.of(new Insurance("Samsung Fire"))
                ))
        );

        // 2. 뚜벅이 유저 (차 없음)
        Person poorPerson = new Person(Optional.empty());

        // 3. 무보험 유저 (차는 있는데 보험 없음)
        Person riskyPerson = new Person(
                Optional.of(new Car(Optional.empty()))
        );

        System.out.println("Rich: " + getInsuranceName(richPerson));   // Samsung Fire
        System.out.println("Poor: " + getInsuranceName(poorPerson));   // Unknown
        System.out.println("Risky: " + getInsuranceName(riskyPerson)); // Unknown
    }

    public static String getInsuranceName(Person person) {
        // [Challenge]
        // 1. person은 null일 수도 있으니 Optional.ofNullable로 시작.
        // 2. person.car()는 Optional<Car>를 반환함 -> 여기서 map? flatMap?
        // 3. car.insurance()도 Optional<Insurance>를 반환함 -> 여기서 map? flatMap?

        return Optional.ofNullable(person)
                // TODO: 여기에 체이닝 코드를 작성하세요.
                .flatMap(Person::car) // 첫번쨰 faltMap으로 car 객체를 펼침
                .flatMap(Car::insurance) // 두번째 flatMap 으로 Insurance 객체를 펼침
                .map(Insurance::companyName) // 펼친 Insurance 객체의 companyName 값 반환
                .orElse("Unknown"); // 없을 때는 Unknown 호출
    }
}
