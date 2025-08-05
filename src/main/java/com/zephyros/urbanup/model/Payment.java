package com.zephyros.urbanup.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    private Task task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;
    
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Amount must be positive")
    private BigDecimal amount;
    
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Platform fee must be positive")
    private BigDecimal platformFee;
    
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Net amount must be positive")
    private BigDecimal netAmount; // Amount after platform fee
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "external_payment_id")
    private String externalPaymentId; // Stripe/Razorpay payment ID
    
    @Column(name = "external_transfer_id")
    private String externalTransferId; // For payout to fulfiller
    
    @Column(name = "escrow_reference")
    private String escrowReference;
    
    @Column(name = "payment_initiated_at")
    private LocalDateTime paymentInitiatedAt;
    
    @Column(name = "payment_captured_at")
    private LocalDateTime paymentCapturedAt;
    
    @Column(name = "payment_released_at")
    private LocalDateTime paymentReleasedAt;
    
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum PaymentStatus {
        PENDING("Pending"),
        AUTHORIZED("Authorized"), // Payment method verified, amount reserved
        CAPTURED("Captured"), // Money captured in escrow
        RELEASED("Released"), // Money released to fulfiller
        REFUNDED("Refunded"), // Money refunded to poster
        FAILED("Failed"),
        DISPUTED("Disputed");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentMethod {
        UPI("UPI"),
        CARD("Credit/Debit Card"),
        NET_BANKING("Net Banking"),
        WALLET("Digital Wallet"),
        BANK_TRANSFER("Bank Transfer");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Payment() {}
    
    public Payment(Task task, User payer, User payee, BigDecimal amount, 
                   BigDecimal platformFee, PaymentMethod paymentMethod) {
        this.task = task;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.platformFee = platformFee;
        this.netAmount = amount.subtract(platformFee);
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    
    public User getPayer() { return payer; }
    public void setPayer(User payer) { this.payer = payer; }
    
    public User getPayee() { return payee; }
    public void setPayee(User payee) { this.payee = payee; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getExternalPaymentId() { return externalPaymentId; }
    public void setExternalPaymentId(String externalPaymentId) { this.externalPaymentId = externalPaymentId; }
    
    public String getExternalTransferId() { return externalTransferId; }
    public void setExternalTransferId(String externalTransferId) { this.externalTransferId = externalTransferId; }
    
    public String getEscrowReference() { return escrowReference; }
    public void setEscrowReference(String escrowReference) { this.escrowReference = escrowReference; }
    
    public LocalDateTime getPaymentInitiatedAt() { return paymentInitiatedAt; }
    public void setPaymentInitiatedAt(LocalDateTime paymentInitiatedAt) { this.paymentInitiatedAt = paymentInitiatedAt; }
    
    public LocalDateTime getPaymentCapturedAt() { return paymentCapturedAt; }
    public void setPaymentCapturedAt(LocalDateTime paymentCapturedAt) { this.paymentCapturedAt = paymentCapturedAt; }
    
    public LocalDateTime getPaymentReleasedAt() { return paymentReleasedAt; }
    public void setPaymentReleasedAt(LocalDateTime paymentReleasedAt) { this.paymentReleasedAt = paymentReleasedAt; }
    
    public LocalDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isSuccessful() {
        return status == PaymentStatus.CAPTURED || status == PaymentStatus.RELEASED;
    }
    
    public boolean isInEscrow() {
        return status == PaymentStatus.CAPTURED;
    }
    
    public boolean canBeReleased() {
        return status == PaymentStatus.CAPTURED;
    }
    
    public boolean canBeRefunded() {
        return status == PaymentStatus.CAPTURED || status == PaymentStatus.AUTHORIZED;
    }
    
    public void markAsCaptured(String externalPaymentId) {
        this.status = PaymentStatus.CAPTURED;
        this.externalPaymentId = externalPaymentId;
        this.paymentCapturedAt = LocalDateTime.now();
    }
    
    public void markAsReleased(String externalTransferId) {
        this.status = PaymentStatus.RELEASED;
        this.externalTransferId = externalTransferId;
        this.paymentReleasedAt = LocalDateTime.now();
    }
    
    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }
    
    @PreUpdate
    public void setLastModifiedDate() {
        this.updatedAt = LocalDateTime.now();
    }
}
