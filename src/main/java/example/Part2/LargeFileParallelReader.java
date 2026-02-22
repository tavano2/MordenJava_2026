package example.Part2;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LargeFileParallelReader {

    // 100GB 파일을 가정
    public void runParallel(String filePath) throws Exception {
        int threadCount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        long fileSize = file.length();
        long sectionSize = fileSize / threadCount;

        for (int i = 0; i < threadCount; i++) {
            long start = i * sectionSize;
            long end = (i == threadCount - 1) ? fileSize : (start + sectionSize);

            // 각 스레드에게 "너는 대략 start ~ end 구간을 맡아"라고 지시
            executor.submit(() -> processSection(filePath, start, end));
        }
    }

    // 각 스레드가 실행하는 메서드
    private void processSection(String filePath, long start, long end) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel channel = file.getChannel()) {

            // 매핑할 때, 경계 처리를 위해 'end'보다 약간 더 넉넉하게(예: +1KB) 매핑하거나
            // 로직상으로 처리해야 합니다. 여기서는 로직으로 풉니다.

            // [핵심 1: 시작 위치 보정]
            // 0번 스레드가 아니면, 이전 스레드가 읽다 만 부분일 수 있으므로
            // 첫 번째 '\n'이 나올 때까지 건너뜁니다.
            if (start > 0) {
                file.seek(start);
                // 한 바이트씩 읽으며 '\n' 찾기 (간단한 구현을 위해 read() 사용)
                // 실제 고성능에선 작은 버퍼를 사용합니다.
                while (start < end) {
                    int b = file.read();
                    start++;
                    if (b == '\n') {
                        break; // 찾았다! 여기서부터가 내 진짜 시작점
                    }
                }
            }

            // [핵심 2: 데이터 처리 및 종료 위치 보정]
            // 보정된 start 위치부터 읽기 시작
            // MappedByteBuffer는 2GB 제한이 있어 루프를 돌며 매핑해야 하지만,
            // 여기선 개념 전달을 위해 해당 구간을 바로 읽는다고 가정합니다.

            file.seek(start); // 진짜 시작 위치로 이동

            long currentPos = start;
            while (currentPos < file.length()) { // 파일 끝까지 갈 수 있음 주의
                int b = file.read();
                currentPos++;

                // 여기서 데이터를 처리합니다 (예: 라인 버퍼에 담기)
                // processByte(b);

                if (b == '\n') {
                    // 한 줄이 끝났을 때, 내가 맡은 구역(end)을 넘었는지 확인
                    if (currentPos >= end) {
                        break; // 내 구역 끝났고, 문장도 마무리했으니 종료! 🏁
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
