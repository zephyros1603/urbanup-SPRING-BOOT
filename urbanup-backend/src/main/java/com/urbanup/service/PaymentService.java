package com.urbanup.service;

import com.urbanup.dto.request.PaymentIntentRequest;
import com.urbanup.dto.response.PaymentResponse;
import com.urbanup.entity.Payment;
import com.urbanup.exception.PaymentException;
import com.urbanup.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentResponse createPaymentIntent(PaymentIntentRequest request) {
        try {
            // Logic to create a payment intent
            Payment payment = new Payment();
            // Set payment details from request
            payment.setAmount(request.getAmount());
            payment.setCurrency(request.getCurrency());
            // Save payment to the database
            paymentRepository.save(payment);
            return new PaymentResponse(payment.getId(), "Payment intent created successfully");
        } catch (Exception e) {
            throw new PaymentException("Failed to create payment intent", e);
        }
    }

    public PaymentResponse confirmPayment(String paymentId) {
        try {
            // Logic to confirm a payment
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentException("Payment not found"));
            // Update payment status to confirmed
            payment.setStatus("CONFIRMED");
            paymentRepository.save(payment);
            return new PaymentResponse(payment.getId(), "Payment confirmed successfully");
        } catch (Exception e) {
            throw new PaymentException("Failed to confirm payment", e);
        }
    }

    public PaymentResponse refundPayment(String paymentId) {
        try {
            // Logic to refund a payment
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentException("Payment not found"));
            // Update payment status to refunded
            payment.setStatus("REFUNDED");
            paymentRepository.save(payment);
            return new PaymentResponse(payment.getId(), "Payment refunded successfully");
        } catch (Exception e) {
            throw new PaymentException("Failed to refund payment", e);
        }
    }

    public List<Payment> getUserPayments(String userId) {
        // Logic to retrieve payments for a user
        return paymentRepository.findByUserId(userId);
    }

    public Payment getPaymentDetails(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));
    }
}