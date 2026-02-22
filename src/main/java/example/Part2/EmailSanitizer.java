package example.Part2;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EmailSanitizer {
    public static void main(String[] args) {
        List<String> rawEmails = Arrays.asList(
                "user1@test.com",
                null,                // Null Pointer 위험!
                "user2@test.com",
                "",                  // 빈 문자열
                "admin@test.com"
        );

        long validCount = countValidEmails(rawEmails);

        System.out.println("Valid Emails: " + validCount);
        // 예상 결과: 3
    }

    public static long countValidEmails(List<String> emails) {
        return emails.stream()
                // Step 1: 여기서 map을 이용해 각 요소를 Optional로 감싸세요 (ofNullable 사용)
                // .map( ... )
                .map(Optional::ofNullable)
                // Step 2: filter를 이용해 "값이 존재하고(isPresent)" && "비어있지 않은 것"만 남기세요
                // .filter( ... )
                .filter(s -> s.isPresent() && !s.get().isEmpty())
                // Step 3: 개수 카운트
                .count();
    }
}
