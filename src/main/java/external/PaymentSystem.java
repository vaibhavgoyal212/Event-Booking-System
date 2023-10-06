package external;

public interface PaymentSystem {
    boolean processPayment(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount);

    boolean processRefund(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount);
}
