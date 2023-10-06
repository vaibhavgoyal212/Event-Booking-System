package logging;

public class LogStatus {

    public enum AddEventPerformanceLogStatus {
        ADD_PERFORMANCE_SUCCESS,
        ADD_PERFORMANCE_INVALID_FIELDS,
        ADD_PERFORMANCE_SAME_PERFORMANCE_EXIST,
    }

    public enum BookEventLogStatus {
        BOOK_EVENT_SUCCESS,
        BOOK_EVENT_PAYMENT_FAILED,
        BOOK_EVENT_BOOKINGS_NOT_ACCEPTED,
        BOOK_EVENT_INVALID_BOOKING_REQUEST,
    }

    public enum CancelBookingLogStatus {
        CANCEL_BOOKING_SUCCESS,
        CANCEL_BOOKING_BOOKING_NOT_FOUND,
        CANCEL_BOOKING_REFUND_FAILED,
        CANCEL_BOOKING_INVALID_BOOKING_REQUEST
    }

    public enum CancelEventLogStatus {
        CANCEL_EVENT_EVENT_NOT_FOUND,
        CANCEL_EVENT_REFUND_SPONSORSHIP_SUCCESS,
        CANCEL_EVENT_REFUND_SPONSORSHIP_FAILED,
        CANCELLATION_INVALID,
        CANCEL_EVENT_FAILED_PERFORMANCE_ALREADY_STARTED,
        BOOKING_REFUND_FAILED,
        BOOKING_REFUND_SUCCESS,
        CANCELLATION_COMPLETE
    }

    public enum CreateNonTicketedEventLogStatus {
        EVENT_FIELDS_CANNOT_BE_EMPTY,
        SAME_EVENT_EXISTS,
        CREATE_NON_TICKETED_EVENT_SUCCESS,
        CREATE_NON_TICKETED_FAILED,
    }

    public enum CreateTicketedEventLogStatus {
        CREATE_TICKETED_EVENT_SUCCESS,
        CREATE_EVENT_REQUESTED_SPONSORSHIP,
        INVALID_FIELDS,
        SAME_EVENT_EXISTS,
        CREATE_TICKETED_FAILED,
    }

    public enum GetAvailablePerformanceTicketsLogStatus {
        NOT_TICKETED_EVENT,
        PERFORMANCE_NOT_FOUND,
        SUCCESS
    }

    public enum GovernmentReport2LogStatus {
        NO_SUCH_ORGANISATION,
        GOVERNMENT_REPORT2_SUCCESS
    }

    public enum ListConsumerBookingsLogStatus {
        LIST_CONSUMER_BOOKINGS_SUCCESS
    }

    public enum ListEventBookingsLogStatus {
        LIST_EVENT_BOOKINGS_EVENT_NOT_TICKETED,
        LIST_EVENT_BOOKINGS_SUCCESS,
    }

    public enum ListEventsOnGivenDateLogStatus {
        LIST_USER_EVENTS_NO_EVENTS_DATE,
        INVALID_SEARCH_DATE, LIST_USER_EVENTS_FOUND_EVENTS_ON_DATE
    }

    public enum ListEventsLogStatus {
        LIST_USER_EVENTS_SUCCESS,
        LIST_EVENTS_INVALID_USER,
    }

    public enum ListSponsorshipRequestsLogStatus {
        SUCCESS
    }

    public enum LoginLogStatus {
        USER_LOGIN_SUCCESS,
        USER_LOGIN_EMAIL_NOT_REGISTERED,
        USER_LOGIN_WRONG_PASSWORD,
        OTHER_USER_ALREADY_LOGIN
    }

    public enum LogoutLogStatus {
        USER_LOGOUT_SUCCESS,
        USER_LOGOUT_NOT_LOGGED_IN
    }

    public enum RegisterConsumerLogStatus {
        REGISTER_CONSUMER_SUCCESS,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_EMAIL_ALREADY_REGISTERED,
        OTHER_USER_LOGGED_IN, USER_REGISTER_FIELDS_CANNOT_BE_BLANK
    }

    public enum RegisterEntertainmentProviderLogStatus {
        REGISTER_ENTERTAINMENT_PROVIDER_SUCCESS,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_ORG_ALREADY_REGISTERED,
        OTHER_USER_LOGGED_IN,
        USER_REGISTER_FIELDS_CANNOT_BE_BLANK

    }

    public enum RespondSponsorshipLogStatus {
        RESPOND_SPONSORSHIP_REJECT,
        RESPOND_SPONSORSHIP_INVALID_PERCENTAGE,
        RESPOND_SPONSORSHIP_INVALID_REQUEST,
        RESPOND_SPONSORSHIP_All_PERFORMANCES_ENDED,
        RESPOND_SPONSORSHIP_APPROVE_PAYMENT_SUCCESS,
        RESPOND_SPONSORSHIP_APPROVE_PAYMENT_FAILED
    }

    public enum UpdateConsumerProfileLogStatus {
        USER_UPDATE_PROFILE_NOT_CONSUMER,
        USER_UPDATE_PROFILE_SUCCESS,
        USER_UPDATE_PROFILE_FIELDS_INVALID
    }

    public enum UpdateEntertainmentProviderProfileLogStatus {
        USER_UPDATE_PROFILE_SUCCESS,
        USER_UPDATE_PROFILE_ORG_ALREADY_REGISTERED,
        USER_UPDATE_PROFILE_FIELD_INVALID,
        USER_UPDATE_PROFILE_NOT_PROVIDER
    }

    public enum UpdateProfileLogStatus {
        USER_UPDATE_PROFILE_WRONG_PASSWORD,
        USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE,
        USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL_OR_EMPTY
    }

    public enum General {
        EVENT_NOT_FOUND,
        NOT_ENTERTAINMENT_PROVIDER,
        NOT_EVENT_ORGANISER,
        INVALID_USER,
        USER_NOT_LOGGED_IN
    }
}
