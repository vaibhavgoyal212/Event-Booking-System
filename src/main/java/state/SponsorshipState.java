package state;

import model.SponsorshipRequest;
import model.SponsorshipStatus;
import model.TicketedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

public class SponsorshipState implements ISponsorshipState {
    private long nextRequestNumber;
    private final List<SponsorshipRequest> sponsorshipRequests;

    public SponsorshipState() {
        sponsorshipRequests = new ArrayList<>();
        nextRequestNumber = 1;
    }

    public SponsorshipState(ISponsorshipState other) {
        assertNotNull(other);

        sponsorshipRequests = new ArrayList<>();
        nextRequestNumber = other.getAllSponsorshipRequests().size() + 1;
        sponsorshipRequests.addAll(other.getAllSponsorshipRequests());
    }

    public SponsorshipRequest addSponsorshipRequest(TicketedEvent event) {
        if (event == null) {
            return null;
        }

        SponsorshipRequest newRequest = new SponsorshipRequest(nextRequestNumber, event);
        sponsorshipRequests.add(newRequest);

        nextRequestNumber++;
        return newRequest;
    }

    public SponsorshipRequest findRequestByNumber(long requestNumber) {
        for (SponsorshipRequest request : sponsorshipRequests) {
            if (request.getRequestNumber() == requestNumber) {
                return request;
            }
        }

        return null;
    }

    public List<SponsorshipRequest> getAllSponsorshipRequests() {
        return sponsorshipRequests;
    }

    public List<SponsorshipRequest> getPendingSponsorshipRequests() {
        return sponsorshipRequests.stream().filter(
                (SponsorshipRequest r) -> (r.getStatus() == SponsorshipStatus.PENDING))
                .collect(Collectors.toList());
    }
}
