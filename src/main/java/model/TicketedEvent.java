package model;

public class TicketedEvent extends Event {
    private final double ticketPrice;
    private final int numTickets;
    private SponsorshipRequest sponsorshipRequest;

    public TicketedEvent(long eventNumber, EntertainmentProvider organiser, String title, EventType type, double ticketPrice, int numTickets) {
        super(eventNumber, organiser, title, type);

        this.ticketPrice = ticketPrice;
        this.numTickets = numTickets;
    }

    public double getOriginalTicketPrice() {
        return this.ticketPrice;
    }

    public double getDiscountedTicketPrice() {
        return this.isSponsored() ? ticketPrice * (100 - sponsorshipRequest.getSponsoredPricePercent()) / 100 : ticketPrice;
    }

    public int getNumTickets() {
        return this.numTickets;
    }

    public String getSponsorAccountEmail() {
        return this.isSponsored() ? sponsorshipRequest.getSponsorAccountEmail() : null;
    }

    public boolean isSponsored() {
        return sponsorshipRequest != null && sponsorshipRequest.getStatus() == SponsorshipStatus.ACCEPTED;
    }

    public void setSponsorshipRequest(SponsorshipRequest sponsorshipRequest) {
        this.sponsorshipRequest = sponsorshipRequest;
    }

    @Override
    public String toString() {
        return "TicketedEvent{" +
                "eventNumber=" + eventNumber + ", Organiser=" + organiser.getOrgName() + ", title=" + title + ", ticket price=" + ticketPrice + "} ";
    }
}
