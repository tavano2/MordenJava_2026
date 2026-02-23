package example.Predicate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CodeKata20260223 {

    /**
     * 개발자 정보를 담는 불변 데이터 객체
     * @param name 이름
     * @param experience 경력 (연차)
     * @param skills 보유 기술 스택
     */
    public record Developer(
            String name,
            int experience,
            List<String> skills
    ) {
        // 혹시 몰라 null 방어 로직이 필요하다면 여기에 컴팩트 생성자를 둘 수 있습니다.
        public Developer {
            if (skills == null) {
                skills = List.of();
            }
        }

        public boolean isSeniorJavaDev() {
            return experience >= 8 && skills.contains("Java");
        }

        public String getNormalizedName() {
            return (name == null) ? "Unknown" : name.toUpperCase();
        }
    }

    public List<String> getSeniorDeveloperNames(List<Developer> developers) {
        return developers.stream()
                .filter(Developer::isSeniorJavaDev)
                .map(Developer::getNormalizedName)
                .distinct()
                .sorted()
                .toList();
    }

    // 아래와 같이 스태틱 메서드로 뺴는 것 보다는 객체 자체에 묻는 방식(Method Reference)이 더 선형적으로 읽힘
    private static String getDeveloper(Developer d) {
        if (d.name() == null) return "Unknown";
        return d.name().toUpperCase();
    }

    private static Predicate<Developer> getDeveloperPredicate() {
        return d -> d.experience() >= 8 && d.skills().contains("Java");
    }

    // 미션 2
    // 상황
    // 팀원이 작성한 아래 코드는 null 처리를 위해 Optional을 썼지만, 오히려 스트림 중간에 isPresent()와 get()을 남발하여 가독성을 해치고 있습니다.
    // 8년 차 시니어인 당신은 이 코드를 **"함수형 파이프라인이 끊기지 않는 선언형 코드"**로 리팩토링해야 합니다.

    public record Manager(String name, String email) {
        public boolean hasValidEmail() {
            return email != null && !email.isBlank();
        }
    }

    public record Department(String name, Manager manager) {

    }

    public List<String> getManagerEmails(List<Department> departments) {
        return departments.stream()
                .map(d -> Optional.ofNullable(d.manager()))
                .flatMap(Optional::stream)
                .filter(Manager::hasValidEmail)
                .map(Manager::email)
                .toList();
    }

    public static void optionalCode() {
        List<String> inputs = Arrays.asList("java", null, "spring", null, "kotlin");
        List<String> result =
                inputs.stream()
                        .flatMap(s -> Optional.ofNullable(s).stream())
                        .map(String::toUpperCase)
                        .toList();
        System.out.println(result);
    }

    public static void main(String[] args) {
        optionalCode();
    }


}
