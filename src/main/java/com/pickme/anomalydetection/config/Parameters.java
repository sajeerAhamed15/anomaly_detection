package com.pickme.anomalydetection.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:application.properties")
@Component
public class Parameters {

    public static int timeWindow;

    public static int timeWindowForPendingRequests;

    public static String timeZone;

    public static int maxPending;

    public static float maxTotFailureRatio;

    public static float maxIndividualErrorRatio;

    public static String EmailUrl;

    public static String emailaddress;

    public static String SMSUrl;

    public static String countryCode;

    public static String allContactNumbers;

    public static int badRequestCountWindow;

    public static int badRequestTimeWindow;

    @Value( "${timeWindow}" )
    public void setTimeWindow(int timeWindow) {
        Parameters.timeWindow = timeWindow;
    }

    @Value( "${timeWindowForPendingRequests}" )
    public void setTimeWindowForPendingRequests(int timeWindowForPendingRequests) {
        Parameters.timeWindowForPendingRequests = timeWindowForPendingRequests;
    }

    @Value( "${timeZone}" )
    public void setTimeZone(String timeZone) {
        Parameters.timeZone = timeZone;
    }

    @Value( "${maxPending}" )
    public void setMaxPending(int maxPending) {
        Parameters.maxPending = maxPending;
    }

    @Value( "${totFailureRatio}" )
    public void setMaxTotFailureRatio(float maxTotFailureRatio) {
        Parameters.maxTotFailureRatio = maxTotFailureRatio;
    }

    @Value( "${individualErrorRatio}" )
    public void setMaxIndividualErrorRatio(float maxIndividualErrorRatio) {
        Parameters.maxIndividualErrorRatio = maxIndividualErrorRatio;
    }

    @Value( "${email.url}" )
    public void setEmailUrl(String emailUrl) {
        EmailUrl = emailUrl;
    }

    @Value( "${email.emailaddress}" )
    public void setEmailaddress(String emailaddress) {
        Parameters.emailaddress = emailaddress;
    }

    @Value( "${sms.url}" )
    public void setSMSUrl(String SMSUrl) {
        Parameters.SMSUrl = SMSUrl;
    }

    @Value( "${sms.countryCode}" )
    public void setCountryCode(String countryCode) {
        Parameters.countryCode = countryCode;
    }

    @Value( "${sms.contactNumbers}" )
    public void setAllContactNumbers(String allContactNumbers) {
        Parameters.allContactNumbers = allContactNumbers;
    }

    @Value( "${badRequestCountWindow}" )
    public void setBadRequestCountWindow(int badRequestCountWindow) {
        Parameters.badRequestCountWindow = badRequestCountWindow;
    }

    @Value( "${badRequestTimeWindow}" )
    public void setBadRequestTimeWindow(int badRequestTimeWindow) {
        Parameters.badRequestTimeWindow = badRequestTimeWindow;
    }
}
