package example.Part3_5;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;

/**
 * [Week 1 - 2교시 - Step 2]
 * Heap Buffer vs Direct Buffer 성능 측정 실험
 */
public class BufferPerformanceExperiment {

    private static final int FILE_SIZE = 1000 * 1024 * 1024; // 100MB
    private static final int BUFFER_SIZE = 1024 * 8;       // 8KB 버퍼

    // 실험 결과 저장을 위한 Record (Java 17)
    record BenchmarkResult(String type, long durationMs) {}

    public static void main(String[] args) throws IOException {
        System.out.println("=== NIO Buffer Performance Challenge ===");
        System.out.println("대상 데이터 크기: " + (FILE_SIZE / (1024 * 1024)) + "MB\n");

        // 1. Heap Buffer 실험
        BenchmarkResult heapResult = runExperiment(false);
        printResult(heapResult);

        // 2. Direct Buffer 실험
        BenchmarkResult directResult = runExperiment(true);
        printResult(directResult);

        System.out.println("\n[결과 분석]");
        System.out.printf("Direct Buffer가 Heap Buffer보다 약 %.2f배 빠릅니다.%n",
                (double) heapResult.durationMs() / directResult.durationMs());
    }

    private static BenchmarkResult runExperiment(boolean isDirect) throws IOException {
        Path path = Files.createTempFile("nio-test-", ".dat");

        // 미리 채워진 버퍼 준비 (할당 및 데이터 채우기 시간 제외)
        ByteBuffer heapBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        String type = isDirect ? "Direct Buffer" : "Heap Buffer";
        Instant start = Instant.now();

        byte[] pattern = new byte[BUFFER_SIZE];
        java.util.Arrays.fill(pattern, (byte)'A');

        heapBuffer.put(pattern);
        directBuffer.put(pattern);

        ByteBuffer rsBuffer = isDirect ? directBuffer : heapBuffer;

        long result = 0;
        // FileChannel을 사용하여 쓰기 작업 수행
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            result = runPureIoBenchmark(channel, rsBuffer);
        } finally {
            Files.deleteIfExists(path); // 실험용 임시 파일 삭제
        }



        return new BenchmarkResult(type, result);
    }

    private static long runPureIoBenchmark(FileChannel channel, ByteBuffer buffer) throws IOException {
        long start = System.nanoTime();
        // 루프 진입 전, 쓰기 모드 -> 읽기 모드로 딱 한 번 전환
        buffer.flip();
        for (int i = 0; i < (FILE_SIZE / BUFFER_SIZE); i++) {
            channel.write(buffer);
            buffer.rewind(); // 다시 처음부터 읽을 수 있게 포인터만 리셋 (데이터 유지)
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void printResult(BenchmarkResult result) {
        System.out.printf("[%s] 소요 시간: %d ms%n", result.type(), result.durationMs());
    }

    public static void copyFile(Path source, Path target) throws IOException {
        try (FileChannel srcChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(target, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            long position = 0;
            long count = srcChannel.size();

            // [Mission] srcChannel에서 destChannel로 데이터를 바로 쏘는 코드를 한 줄 작성해 보세요.
            // 힌트: srcChannel.transferTo(...)
            srcChannel.transferTo(position, count, destChannel);

        }
    }
}
