package external;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MockPaymentSystem implements PaymentSystem {
    private final Map<String, Map<String, List<Transaction>>> transactions;

    public MockPaymentSystem() {
        transactions = new HashMap<>();
    }

    public boolean processPayment(String buyerAccountEmail, String sellerAccountEmail,
                                  double transactionAmount) {
        if (buyerAccountEmail == null || sellerAccountEmail == null || transactionAmount <= 0 ||
                buyerAccountEmail.equals(sellerAccountEmail)) {
            return false;
        }

        Transaction newTransaction = new Transaction(buyerAccountEmail, sellerAccountEmail, transactionAmount);


        //update transaction map as necessary
        if (!transactions.containsKey(buyerAccountEmail)) {
            transactions.put(buyerAccountEmail, new HashMap<>());
        }

        if (!transactions.containsKey(sellerAccountEmail)) {
            transactions.put(sellerAccountEmail, new HashMap<>());
        }

        if (!transactions.get(buyerAccountEmail).containsKey(sellerAccountEmail)) {
            transactions.get(buyerAccountEmail).put(sellerAccountEmail, new ArrayList<>());
        }

        if (!transactions.get(sellerAccountEmail).containsKey(buyerAccountEmail)) {
            transactions.get(sellerAccountEmail).put(buyerAccountEmail, new ArrayList<>());
        }

        //get all transactions between the buyer and seller
        List<Transaction> prevTransactions = transactions.get(buyerAccountEmail).get(sellerAccountEmail);
        List<Transaction> prevReverseTransactions = transactions.get(sellerAccountEmail).get(buyerAccountEmail);

        //count suspicious transactions
        int suspiciousTransactions = prevTransactions.stream().filter(
                    (Transaction t) -> (t.amount == transactionAmount)
                ).collect(Collectors.toList()).size();

        suspiciousTransactions += prevReverseTransactions.stream().filter(
                (Transaction t) -> (t.amount == transactionAmount)
        ).collect(Collectors.toList()).size();

        if (suspiciousTransactions >= 10) {
            return false;
        }

        transactions.get(buyerAccountEmail).get(sellerAccountEmail).add(newTransaction);
        return true;
    }

    public boolean processRefund(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount) {
        if (!transactions.containsKey(buyerAccountEmail) ||
            !transactions.get(buyerAccountEmail).containsKey(sellerAccountEmail)) {
            return false;
        }

        //update transactions map as necessary
        if (!transactions.containsKey(sellerAccountEmail)) {
            transactions.put(sellerAccountEmail, new HashMap<>());
        }

        if (!transactions.get(sellerAccountEmail).containsKey(buyerAccountEmail)) {
            transactions.get(sellerAccountEmail).put(buyerAccountEmail, new ArrayList<>());
        }

        // find a matching transaction to refund if any
        for (Transaction t : transactions.get(buyerAccountEmail).get(sellerAccountEmail)) {
            if (t.amount == transactionAmount && !t.refunded) {
                t.refunded = true;
                Transaction refundTransaction = new Transaction(sellerAccountEmail,
                        buyerAccountEmail, transactionAmount);
                refundTransaction.refunded = true;
                transactions.get(sellerAccountEmail).get(buyerAccountEmail).add(refundTransaction);
                return true;
            }
        }
        return false;
    }

    private static class Transaction {
        private final String buyerEmail;
        private final String sellerEmail;
        private final double amount;
        private boolean refunded;

        public Transaction(String buyerEmail, String sellerEmail, double amount) {
            this.buyerEmail = buyerEmail;
            this.sellerEmail = sellerEmail;
            this.amount = amount;
            this.refunded = false;
        }
    }
}
