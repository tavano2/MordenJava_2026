package Part3;
import java.util.Optional;

public class UserValidationService {

    record User(String name, int age, String email) {}

    /*
    시나리오:
    유저(User) 정보를 조회한다.
    유저가 존재하고(isPresent), 나이가 20세 이상이어야 하며, 이메일이 **"@korea.com"**으로 끝나야 한다.
    이 조건을 모두 만족하면 이메일 주소를 반환하고, 하나라도 만족하지 않거나 유저가 없으면 SecurityException을 던져라.
     */
    public static String getValidUserEmail(User user) {
        return Optional.ofNullable(user)
                // TODO: filter를 두 번 사용해서 조건을 걸어보게.
                // 1. 나이 20세 이상
                // 2. 이메일이 @korea.com으로 끝남
                .filter(u -> u.age() >= 20 && "@korea.com".endsWith(u.email()))
                // 3. 최종적으로 이메일 추출 (map)
                .map(User::email)
                // 4. 없으면 예외 발생 (orElseThrow)
                .orElseThrow(() -> new SecurityException("유효하지 않은 유저입니다."));
    }

    public static String getValidUserEmailS(User user) {
        return Optional.ofNullable(user)
                // 1. 나이 조건 분리: "나이가 20살 이상이고..."
                .filter(u -> u.age() >= 20)

                // 2. 이메일 조건 분리: "이메일이 존재하며 @korea.com으로 끝나고..."
                // (주의: u.email()이 null일 수도 있으니 null safe하게 작성)
                .filter(u -> u.email() != null && u.email().endsWith("@korea.com"))

                // 3. 변환: "이메일만 추출해서..."
                .map(User::email)

                // 4. 예외: "없으면 던져라."
                .orElseThrow(() -> new SecurityException("유효하지 않은 유저입니다."));
    }
}
