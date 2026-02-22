package example.Part3;

import java.util.Optional;

public class OptionalFieldPattern {

    // 도메인 모델 (Entity or DTO)
    static class Member {
        private final String name;
        // 1. 필드는 그냥 null을 허용하는 일반 참조로 둔다. (메모리 절약, 직렬화 가능)
        private final String email;

        public Member(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // 2. 외부로 노출할 때(Getter)만 Optional로 감싸서 "없을 수 있음"을 알린다.
        public Optional<String> getEmail() {
            return Optional.ofNullable(email);
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        Member memberWithEmail = new Member("James", "james@code.com");
        Member memberWithoutEmail = new Member("Guest", null);

        // 호출하는 쪽(Client)은 Optional을 받으므로 안전하게 처리 강제됨
        System.out.println("User 1 Email: " +
                memberWithEmail.getEmail().orElse("이메일 없음"));

        System.out.println("User 2 Email: " +
                memberWithoutEmail.getEmail().orElse("이메일 없음"));
    }
}
