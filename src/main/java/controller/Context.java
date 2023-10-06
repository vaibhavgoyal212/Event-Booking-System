package controller;

import external.MockPaymentSystem;
import external.PaymentSystem;
import state.*;

public class Context {
    private final MockPaymentSystem mockPaymentSystem;
    private final IUserState userState;
    private final IEventState eventState;
    private final IBookingState bookingState;
    private final ISponsorshipState sponsorshipState;

    public Context() {
        this.mockPaymentSystem = new MockPaymentSystem();
        this.userState = new UserState();
        this.eventState = new EventState();
        this.bookingState = new BookingState();
        this.sponsorshipState = new SponsorshipState();
    }

    public Context(Context other) {
        this.mockPaymentSystem = other.mockPaymentSystem;
        this.userState = new UserState(other.getUserState());
        this.eventState = new EventState(other.getEventState());
        this.bookingState = new BookingState(other.getBookingState());
        this.sponsorshipState = new SponsorshipState(other.getSponsorshipState());
    }

    public PaymentSystem getPaymentSystem() {
        return this.mockPaymentSystem;
    }

    public IUserState getUserState() {
        return this.userState;
    }

    public IBookingState getBookingState() {
        return this.bookingState;
    }

    public IEventState getEventState() {
        return this.eventState;
    }

    public ISponsorshipState getSponsorshipState() {
        return this.sponsorshipState;
    }
}
