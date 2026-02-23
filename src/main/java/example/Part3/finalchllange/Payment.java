package example.Part3.finalchllange;

public sealed interface Payment
        permits CreditCard, BankTransfer, CryptoCurrency {
    long amount();
}

record CreditCard(String cardId, String cardType, long amount) implements Payment {}
record BankTransfer(String bankCode, String accountNo, long amount) implements Payment {}
record CryptoCurrency(String walletAddress, String coinSymbol, long amount) implements Payment {}