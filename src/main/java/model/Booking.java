package model;

import java.time.LocalDateTime;

public class Booking {
    private final LocalDateTime bookingDateTime;
    private final long bookingNumber;
    private final Consumer booker;
    private final EventPerformance performance;
    private final int numTickets;
    private final double amountPaid;
    private BookingStatus status;

    public Booking(long bookingNumber, Consumer booker, EventPerformance performance, int numTickets,
                   double amountPaid, LocalDateTime bookingDateTime) {
        this.amountPaid = amountPaid;
        this.bookingNumber = bookingNumber;
        this.booker = booker;
        this.performance = performance;
        this.numTickets = numTickets;
        this.bookingDateTime = bookingDateTime;
        this.status = BookingStatus.ACTIVE;
    }

    public long getBookingNumber() {
        return this.bookingNumber;
    }

    public BookingStatus getStatus() {
        return this.status;
    }

    public Consumer getBooker() {
        return this.booker;
    }

    public EventPerformance getEventPerformance() {
        return this.performance;
    }

    public double getAmountPaid() {
        return this.amountPaid;
    }

    public void cancelByConsumer() {
        this.status = BookingStatus.CANCELLED_BY_CONSUMER;
    }

    public void cancelPaymentFailed() {
        this.status = BookingStatus.PAYMENT_FAILED;
    }

    public void cancelByProvider() {
        this.status = BookingStatus.CANCELLED_BY_PROVIDER;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingDateTime=" + bookingDateTime +
                ", bookingNumber=" + bookingNumber +
                ", booker=" + booker +
                ", performance=" + performance +
                ", numTickets=" + numTickets +
                ", amountPaid=" + amountPaid +
                ", status=" + status +
                '}';
    }
}
