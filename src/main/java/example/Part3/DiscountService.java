package example.Part3;
import java.util.Optional;

public class DiscountService {

    record Membership(String grade, int discount) {}

    record Member(String name, Membership membership) {
        // 멤버십이 없을 수도 있음
        public Optional<Membership> getMembership() {
            return Optional.ofNullable(membership);
        }
    }

    // [Legacy] 보기만 해도 숨이 턱 막히는 코드
    public static int getDiscountPercentageLegacy(Member member) {
        if (member != null) {
            // Member.getMembership()은 Optional을 반환하므로 get()을 써야 하는 상황...
            // (Legacy 코드라 가정하고 Optional을 억지로 깠다고 치자)
            if (member.getMembership().isPresent()) {
                Membership ms = member.getMembership().get();
                if (ms != null) {
                    return ms.discount();
                }
            }
        }
        return 0; // 기본값
    }

    // [Challenge] 자네가 채워야 할 부분
    public static int getDiscountPercentageModern(Member member) {
        return Optional.ofNullable(member)
                // 힌트: Member -> Membership (Optional) -> discount (int)
                .flatMap(Member::getMembership)
                .map(Membership::discount)
                .orElse(0);
    }

    public static void main(String[] args) {
        // 테스트 코드는 자네가 직접 작성해서 검증해 볼 것
        Membership memship1 = new Membership("A", 50);
        Membership memship2 = new Membership("B", 45);
        Member mem1 = new Member("LEE", memship1);
        Member mem2 = new Member("PARK", memship2);
        int grade1 = getDiscountPercentageLegacy(mem1);
        var grade2 = getDiscountPercentageModern(mem2);
        System.out.println(grade1);
        System.out.println(grade2);
    }
}
