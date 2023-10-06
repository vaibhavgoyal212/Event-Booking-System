package model;

public class SponsorshipRequest {
    private final long requestNumber;
    private final TicketedEvent event;
    private SponsorshipStatus status;
    private Integer sponsoredPricePercent;
    private String sponsorAccountEmail;

    public SponsorshipRequest(long requestNumber, TicketedEvent event) {
        this.requestNumber = requestNumber;
        this.event = event;
        this.status = SponsorshipStatus.PENDING;
        this.sponsoredPricePercent = 0;
    }

    public long getRequestNumber() {
        return this.requestNumber;
    }

    public TicketedEvent getEvent() {
        return this.event;
    }

    public SponsorshipStatus getStatus() {
        return this.status;
    }

    public Integer getSponsoredPricePercent() {
        return this.sponsoredPricePercent;
    }

    public void setSponsoredPricePercent(int sponsoredPercent) {
        this.sponsoredPricePercent = sponsoredPercent;
    }

    public String getSponsorAccountEmail() {
        return this.sponsorAccountEmail;
    }

    public void accept(int percent, String sponsorAccountEmail) {
        this.status = SponsorshipStatus.ACCEPTED;
        this.sponsoredPricePercent = percent;
        this.sponsorAccountEmail = sponsorAccountEmail;
    }

    public void reject() {
        this.status = SponsorshipStatus.REJECTED;
    }
}
