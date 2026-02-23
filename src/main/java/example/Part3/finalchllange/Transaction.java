package example.Part3.finalchllange;

public record Transaction(String txId,
                          Payment payment,         // CreditCard, BankTransfer, CryptoCurrency 중 하나
                          TransactionStatus status // Success, Failed 등
                          ) {
}
