package Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class CodeKata20260210 {

    public record User(
            Long id,
            String username,
            String role,    // "ADMIN", "MANAGER", "USER"
            boolean active,
            String department
    ) {}

    public record AccessLog(
            Long userId,
            String resource,
            boolean granted
    ) {}

    // [문제 1] "안전한 관리자 조회" (Optional Pipeline)
    // 사용자가 없거나, 활성화되지 않았거나, 관리자가 아니면 "Access Denied"를 반환하세요.
    public String getActiveAdminName(User user) {
        // 인자로 들어온 user가 null일 수 있음
        if (user != null) {
            if (user.active()) {
                if ("ADMIN".equals(user.role())) {
                    return user.username();
                }
            }
        }
        return "Access Denied";
    }

    // 결과 : S (Perfect Score)
    // 훌륭합니다. **"Optional 파이프라인의 정석"**을 보여주셨습니다. Optional.ofNullable로 시작해서 filter로 조건을 걸러내고,
    // map으로 데이터를 변환한 뒤, orElse로 안전하게 빠져나오는 흐름이 아주 매끄럽습니다.
    // 특히 equalsIgnoreCase를 사용하여 대소문자 문제까지 방어한 점은 8년 차의 연륜이 느껴지는 디테일입니다.
    public String getActiveAdminNameTobe(User user) {
        return Optional.ofNullable(user)
                .filter(User::active)
                .filter(usr -> "ADMIN".equalsIgnoreCase(usr.role()))
                .map(User::username)
                .orElse("Access Denied");
    }

    // [문제 2] "다중 조건 보안 필터링" (Predicate Composition)
    // 특정 부서("HR" 또는 "LEGAL")에 속해 있거나, 부서에 상관없이 "ADMIN" 역할을 가진 사용자들만 필터링하여 리스트로 반환해야 합니다.
    public List<User> getAuthorizedUsers(List<User> users) {
        List<User> authorized = new ArrayList<>();
        for (User u : users) {
            // 리스트 안에 null이 섞여 있을 수 있음
            if (u != null) {
                boolean isSecurityDept = "HR".equals(u.department()) || "LEGAL".equals(u.department());
                boolean isAdmin = "ADMIN".equals(u.role());

                if (isSecurityDept || isAdmin) {
                    authorized.add(u);
                }
            }
        }
        return authorized;
    }

    // 결과 등급: A (Practical & Clean)
    // 아주 실용적입니다. 복잡한 로직을 private 메서드(isHrOrLegal)로 추출(Extract Method)하여 filter 내부를 깔끔하게 유지한 점은 칭찬받아 마땅합니다.
    // 현업에서 동료들이 가장 좋아하는 스타일입니다.
    // 이번 훈련의 목표였던 "함수형 인터페이스(Predicate)의 조립(Composition)" 맛을 조금 더 보여드리고 싶습니다.
    public List<User> getAuthorizedUsersTobe(List<User> users) {
        return users.stream()
                .filter(Objects::nonNull)
                .filter(u -> isHrOrLegal(u) || "ADMIN".equalsIgnoreCase(u.role()))
                .toList();
    }

    private static boolean isHrOrLegal(User u) {
        return "HR".equalsIgnoreCase(u.department()) || "LEGAL".equalsIgnoreCase(u.department());
    }

    // 문제 2 S등급 코드
    // 이 기술은 나중에 동적 쿼리 생성이나 정책(Policy) 패턴을 만들 때 아주 강력해지거든요.
    // 나중에 기획자가 "보안팀도 추가해 주세요" 하면, isSecurity 하나 만들어서 .or(isSecurity)만 뒤에 붙이면 끝입니다.
    // 기존 로직을 건드리지 않고 확장(Open-Closed Principle)하기 아주 좋습니다.
    public List<User> getAuthorizedUsersTobeS(List<User> users) {
        // 1. 재료 준비 (단위 로직)
        Predicate<User> isAdmin = u -> "ADMIN".equalsIgnoreCase(u.role());
        Predicate<User> isHr = u -> "HR".equalsIgnoreCase(u.department());
        Predicate<User> isLegal = u -> "LEGAL".equalsIgnoreCase(u.department());

        // 2. 조립 (Composition) - "관리자 이거나, HR 이거나, 법무팀 이거나"
        // 마치 레고 블록 조립하듯 로직을 쌓을 수 있습니다.
        Predicate<User> isAuthorized = isAdmin.or(isHr).or(isLegal);

        return users.stream()
                .filter(Objects::nonNull)
                .filter(isAuthorized) // <--- 코드가 영어 문장처럼 읽히죠?
                .toList();
    }


    // [문제 3] "로그 생성 및 예외 전략" (Optional.ifPresentOrElse)
    // 액세스 로그를 기록할 때, 사용자가 존재하면 접근 허가 로그(granted: true)를 남기고, 사용자가 없으면 보안 경고를 출력해야 합니다.
    public void logAccessAttempt(User user, String resource, List<AccessLog> accessLogs) {
        if (user != null) {
            AccessLog log = new AccessLog(user.id(), resource, true);
            System.out.println("Access granted for user: " + user.username());
            accessLogs.add(log);
        } else {
            System.out.println("Access attempt by unknown user");
        }
    }

    // 결과 : S (Perfect Execution)
    // 완벽합니다. Java 9에서 추가된 **ifPresentOrElse**의 용도를 정확히 이해하고 계십니다.
    // 자바 8 시절에는 ifPresent만 있어서 else 처리를 하려면 어쩔 수 없이 isPresent()를 확인하거나 orElseGet으로 억지 춘향 식 코드를 짰어야 했죠.
    public void logAccessAttemptTobe(User user, String resource, List<AccessLog> accessLogs) {
        // [Senior Tip] 람다 블록 다이어트
        // 지금도 충분히 훌륭하지만, 현업에서 **가독성(Readability)**을 극한으로 끌어올리는 팁 하나 드립니다.
        // 람다식 내부가 3줄 이상 넘어가면, **별도 메서드로 추출(Extract Method)**하는 것이 좋습니다.
        Optional.ofNullable(user).ifPresentOrElse(
                u -> grantAccess(resource, accessLogs, u) // 1. 성공 시: 메서드 참조로 깔끔하게
                , () -> System.out.println("Access attempt by unknown user")); // 2. 실패 시: 람다 한 줄
    }

    private static void grantAccess(String resource, List<AccessLog> accessLogs, User u) {
        AccessLog log = new AccessLog(u.id(), resource, true);
        System.out.println("Access granted for user: " + u.username());
        accessLogs.add(log);
    }



}
