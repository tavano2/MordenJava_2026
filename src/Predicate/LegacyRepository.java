package Predicate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

// 문제 2: "자원 해제의 정석 (Try-with-resources)"
public class LegacyRepository {

    private final DataSource dataSource;

    // 생성자 주입 (Dependency Injection)
    public LegacyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // [문제 2번 대상 코드]
    public void executeQuery(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            throw e;
        } finally {
            // ... (기존 지저분한 close 로직) ...
        }
    }

    // 결과 : S (Perfect Execution)
    // 총평 : 군더더기가 하나도 없습니다.
    public void executeQueryTobe(String sql) throws SQLException {
        // 경고가 뜨는 이유 SQL Injection(인젝션) 때문
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.execute();
        }
    }
}