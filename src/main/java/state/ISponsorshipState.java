package state;

import model.SponsorshipRequest;
import model.TicketedEvent;

import java.util.List;

public interface ISponsorshipState {
    SponsorshipRequest addSponsorshipRequest(TicketedEvent event);

    SponsorshipRequest findRequestByNumber(long requestNumber);

    List<SponsorshipRequest> getAllSponsorshipRequests();

    List<SponsorshipRequest> getPendingSponsorshipRequests();
}
