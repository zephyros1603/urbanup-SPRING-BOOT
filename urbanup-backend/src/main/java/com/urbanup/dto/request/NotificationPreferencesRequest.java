package com.urbanup.dto.request;

import javax.validation.constraints.NotNull;

public class NotificationPreferencesRequest {

    @NotNull
    private Boolean emailNotifications;

    @NotNull
    private Boolean pushNotifications;

    @NotNull
    private Boolean smsNotifications;

    public NotificationPreferencesRequest() {
    }

    public NotificationPreferencesRequest(Boolean emailNotifications, Boolean pushNotifications, Boolean smsNotifications) {
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
        this.smsNotifications = smsNotifications;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
}