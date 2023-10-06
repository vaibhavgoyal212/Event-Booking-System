package model;

public class ConsumerPreferences {

    public boolean preferSocialDistancing;
    public boolean preferAirFiltration;
    public boolean preferOutdoorsOnly;
    public int preferredMaxCapacity;
    public int preferredMaxVenueSize;

    public ConsumerPreferences() {
        this.preferSocialDistancing = false;
        this.preferAirFiltration = false;
        this.preferOutdoorsOnly = false;
        this.preferredMaxCapacity = 0;
        this.preferredMaxVenueSize = 0;
    }
}
