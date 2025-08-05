package com.urbanup.dto.request;

import javax.validation.constraints.NotNull;

public class PaymentIntentRequest {

    @NotNull
    private String paymentMethodId;

    @NotNull
    private Double amount;

    @NotNull
    private String currency;

    private String description;

    public PaymentIntentRequest() {
    }

    public PaymentIntentRequest(String paymentMethodId, Double amount, String currency, String description) {
        this.paymentMethodId = paymentMethodId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}