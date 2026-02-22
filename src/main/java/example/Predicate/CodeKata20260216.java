package example.Predicate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
// ★ 핵심: 유틸리티 메서드를 static import
import static example.Predicate.util.FunctionUtils.wrap;

public class CodeKata20260216 {

    // 문제 1: "람다와 Checked Exception의 불편한 동거"
    public List<String> readAllFiles(List<Path> paths) {
        return paths.stream()
                .map(path -> {
                    try {
                        return Files.readString(path);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 읽기 실패: " + path, e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<String> readAllFilesTobe(List<Path> paths) {
        return paths.stream()
                .map(wrap(Files::readString))
                .collect(Collectors.toList());
    }

    // 보통은 위 스트림에 대한 예외를 모던시긍로 변경할 시 아래와 같이 먼저 개발한다.
    // 1단계: 람다 안을 깔끔하게
    /*
    ...
    .map(this::readFileSafe) // 메서드 레퍼런스 사용 가능!

    // 별도 메서드 (여기서 예외 처리)
    private String readFileSafe(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
     */

    //

}
