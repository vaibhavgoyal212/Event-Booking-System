package model;

import java.util.ArrayList;
import java.util.List;

public class Consumer extends User {

    private final List<Booking> bookings;
    private ConsumerPreferences consumerPreferences = new ConsumerPreferences();

    private String name;
    private String phoneNumber;

    public Consumer(String name, String email, String phoneNumber,
                    String password, String paymentAccountEmail) {
        super(email, password, paymentAccountEmail);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public ConsumerPreferences getPreferences() {
        return this.consumerPreferences;
    }

    public void setPreferences(ConsumerPreferences preferences) {
        this.consumerPreferences = preferences;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void notify(String message) {
        System.out.println(message);
    }

    public void setPhoneNumber(String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "bookings=" + bookings.size() +
                ", consumerPreferences=" + consumerPreferences.toString() +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
