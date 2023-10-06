package model;

import external.EntertainmentProviderSystem;
import external.MockEntertainmentProviderSystem;
import java.util.ArrayList;
import java.util.List;

public class EntertainmentProvider extends User {
    final EntertainmentProviderSystem providerSystem;
    private String orgName;
    private String orgAddress;
    private final List<Event> events;
    private final String paymentAccountEmail;
    private String mainRepName;
    private String mainRepEmail;
    private final String password;
    private List<String> otherRepNames;
    private List<String> otherRepEmails;

    public EntertainmentProvider(String orgName, String orgAddress, String paymentAccountEmail,
                                 String mainRepName, String mainRepEmail, String password,
                                 List<String> otherRepNames, List<String> otherRepEmails) {
        super(mainRepEmail, password, paymentAccountEmail);
        this.orgAddress = orgAddress;
        this.orgName = orgName;
        this.mainRepName = mainRepName;
        this.otherRepEmails = otherRepEmails;
        this.otherRepNames = otherRepNames;
        this.paymentAccountEmail = paymentAccountEmail;
        this.password = password;
        this.providerSystem = new MockEntertainmentProviderSystem(orgName, orgAddress);
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAddress() {
        return this.orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public List<Event> getEvents() {
        return this.events;
    }

    public void setMainRepName(String mainRepName) {
        this.mainRepName = mainRepName;
    }

    public void setMainRepEmail(String mainRepEmail) {
        this.mainRepEmail = mainRepEmail;
        super.setEmail(mainRepEmail);
    }

    public void setOtherRepNames(List<String> otherRepNames) {
        this.otherRepNames = otherRepNames;
    }

    public void setOtherRepEmails(List<String> otherRepEmails) {
        this.otherRepEmails = otherRepEmails;
    }

    public EntertainmentProviderSystem getProviderSystem() {
        return this.providerSystem;
    }

    @Override
    public String toString() {
        return "EntertainmentProvider{" +
                "orgName='" + orgName + '\'' +
                ", orgAddress='" + orgAddress + '\'' +
                ", events=" + events +
                ", paymentAccountEmail='" + paymentAccountEmail + '\'' +
                ", mainRepName='" + mainRepName + '\'' +
                ", mainRepEmail='" + mainRepEmail + '\'' +
                ", password='" + password + '\'' +
                ", otherRepNames=" + otherRepNames +
                ", otherRepEmails=" + otherRepEmails +
                '}';
    }
}
