package Part3;

public class SwitchExpressionDeepDive {

    enum Day { MONDAY, WEDNESDAY, FRIDAY, SUNDAY }

    public static void main(String[] args) {
        Day today = Day.FRIDAY;

        // [Legacy Statement]
        // 1. result 변수를 미리 선언해야 함 (불변성 깨짐)
        // 2. break 빼먹으면 대참사
        int numLettersLegacy;
        switch (today) {
            case MONDAY:
            case FRIDAY:
            case SUNDAY:
                numLettersLegacy = 6;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + today);
        }
        System.out.println("Legacy: " + numLettersLegacy);


        // [Modern Expression]
        // 1. 변수 선언과 동시에 초기화 (불변성 확보 -> final 가능)
        // 2. break 필요 없음
        // 3. 다중 라벨 (MONDAY, FRIDAY, ...) 지원
        int numLettersModern = switch (today) {
            case MONDAY, WEDNESDAY, FRIDAY, SUNDAY -> 6;
            // 만약 여기에 다른 Enum 값이 추가된다면 컴파일러가 'default'나 케이스 추가를 강제함!
        };
        System.out.println("Modern: " + numLettersModern);


        // [Complex Logic with yield]
        // 값을 계산하기 전에 로그를 찍거나 로직이 필요할 때
        String message = switch (today) {
            case MONDAY -> "Start of week";
            case WEDNESDAY -> "Hello World";
            case FRIDAY -> {
                System.out.println("불금입니다! 퇴근 준비하세요.");
                // return "Weekend"; // 컴파일 에러! (메서드 종료임)
                yield "Weekend Coming Soon"; // 이 블록의 결과값으로 산출
            }
            case SUNDAY -> "Rest";
        };
        System.out.println("Message: " + message);
    }
}
