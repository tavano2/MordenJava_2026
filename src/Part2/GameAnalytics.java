package Part2;
import java.util.Arrays;
import java.util.List;

public class GameAnalytics {

    // 입력 데이터: 한 판의 결과
    record MatchResult(int kills, int deaths, boolean isWin) {}

    // 출력 데이터: 시즌 종합 기록
    record SeasonStats(int totalKills, int totalDeaths, int winCount) {

        // 헬퍼 메서드: 매치 하나를 내 기록에 더함 (Accumulator용)
        public SeasonStats add(MatchResult match) {
            return new SeasonStats(
                    this.totalKills + match.kills(),
                    this.totalDeaths + match.deaths(),
                    this.winCount + (match.isWin() ? 1 : 0)
            );
        }

        // 헬퍼 메서드: 다른 기록과 합침 (Combiner용)
        public SeasonStats merge(SeasonStats other) {
            return new SeasonStats(
                    this.totalKills + other.totalKills(),
                    this.totalDeaths + other.totalDeaths(),
                    this.winCount + other.winCount()
            );
        }
    }

    public static void main(String[] args) {
        List<MatchResult> seasonMatches = Arrays.asList(
                new MatchResult(10, 2, true),  // Win
                new MatchResult(5, 5, false),  // Loss
                new MatchResult(12, 0, true),  // Win
                new MatchResult(8, 4, false)   // Loss
        );

        SeasonStats finalStats = calculateSeasonStats(seasonMatches);

        System.out.println("Total Kills: " + finalStats.totalKills()); // 예상: 35
        System.out.println("Total Wins: " + finalStats.winCount());   // 예상: 2
    }

    public static SeasonStats calculateSeasonStats(List<MatchResult> matches) {
        // TODO: matches.parallelStream().reduce(...) 를 사용하여 코드를 완성하세요.
        // 헬퍼 메서드(add, merge)를 활용하면 코드가 깔끔해집니다.

        return matches.parallelStream().reduce(
                // 1. Identity: 초기값 설정 (Logic의 핵심!)
                new SeasonStats(0, 0, 0),
                // 2. Accumulator
                (SeasonStats::add),
                // 3. Combiner
                (SeasonStats::merge)
        ); // 여기를 지우고 작성하세요
    }
}
