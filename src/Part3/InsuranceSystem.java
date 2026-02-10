package Part3;
import java.util.Optional;

public class InsuranceSystem {

    public record Person(String name, Car car) {
        Optional<Car> getCar(){
            return Optional.ofNullable(car);
        }
    }
    public record Car(Insurance insurance) {
        Optional<Insurance> getInsurance() {
            return Optional.ofNullable(insurance);
        }
    }
    public record Insurance(String name) {}

    public static String getInsuranceName(Person person) {
        return Optional.ofNullable(person)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::name).orElse("Unknown Insurance");
    }

}
