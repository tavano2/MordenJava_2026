package example.Part3.finalchllange;

import java.time.LocalDateTime;

public sealed interface TransactionStatus
        permits Success, Failed, Pending, Refunded {}

record Success(LocalDateTime completedAt) implements TransactionStatus {}
record Failed(String errorCode, String errorMessage) implements TransactionStatus {}
record Pending(LocalDateTime startedAt) implements TransactionStatus {}
record Refunded(String reason, String originalTxId) implements TransactionStatus {}