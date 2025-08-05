package com.zephyros.urbanup.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Payment;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payment by task
    Optional<Payment> findByTask(Task task);
    
    Optional<Payment> findByTaskId(Long taskId);
    
    // Find payments by users
    List<Payment> findByPayerOrderByCreatedAtDesc(User payer);
    
    List<Payment> findByPayeeOrderByCreatedAtDesc(User payee);
    
    // Find payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    Page<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status, Pageable pageable);
    
    // Find payments by method
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    // User-specific payment queries
    @Query("SELECT p FROM Payment p WHERE p.payer = :user OR p.payee = :user ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByUser(@Param("user") User user);
    
    @Query("SELECT p FROM Payment p WHERE (p.payer = :user OR p.payee = :user) AND p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByUserAndStatus(@Param("user") User user, @Param("status") Payment.PaymentStatus status);
    
    // Escrow payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'CAPTURED' ORDER BY p.paymentCapturedAt DESC")
    List<Payment> findPaymentsInEscrow();
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'CAPTURED' AND p.paymentCapturedAt <= :threshold")
    List<Payment> findOldEscrowPayments(@Param("threshold") LocalDateTime threshold);
    
    // Failed payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' ORDER BY p.createdAt DESC")
    List<Payment> findFailedPayments();
    
    @Query("SELECT p FROM Payment p WHERE p.payer = :user AND p.status = 'FAILED' ORDER BY p.createdAt DESC")
    List<Payment> findFailedPaymentsByPayer(@Param("user") User user);
    
    // Successful payments
    @Query("SELECT p FROM Payment p WHERE p.status IN ('CAPTURED', 'RELEASED') ORDER BY p.createdAt DESC")
    List<Payment> findSuccessfulPayments();
    
    // External payment ID queries
    Optional<Payment> findByExternalPaymentId(String externalPaymentId);
    
    Optional<Payment> findByExternalTransferId(String externalTransferId);
    
    List<Payment> findByEscrowReference(String escrowReference);
    
    // Amount-based queries
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount ORDER BY p.amount DESC")
    List<Payment> findHighValuePayments(@Param("minAmount") BigDecimal minAmount);
    
    @Query("SELECT p FROM Payment p WHERE p.amount <= :maxAmount ORDER BY p.amount ASC")
    List<Payment> findLowValuePayments(@Param("maxAmount") BigDecimal maxAmount);
    
    // Time-based queries
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<Payment> findRecentPayments(@Param("since") LocalDateTime since);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentCapturedAt >= :since ORDER BY p.paymentCapturedAt DESC")
    List<Payment> findRecentlyCapturedPayments(@Param("since") LocalDateTime since);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentReleasedAt >= :since ORDER BY p.paymentReleasedAt DESC")
    List<Payment> findRecentlyReleasedPayments(@Param("since") LocalDateTime since);
    
    // Platform fee queries
    @Query("SELECT SUM(p.platformFee) FROM Payment p WHERE p.status IN ('CAPTURED', 'RELEASED') AND p.paymentCapturedAt >= :startDate AND p.paymentCapturedAt <= :endDate")
    BigDecimal getTotalPlatformFeeBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.platformFee) FROM Payment p WHERE p.status IN ('CAPTURED', 'RELEASED')")
    BigDecimal getTotalPlatformFeeAllTime();
    
    // Analytics and statistics
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countPaymentsByStatus(@Param("status") Payment.PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = :method")
    Long countPaymentsByMethod(@Param("method") Payment.PaymentMethod method);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    Long countPaymentsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(p.amount) FROM Payment p WHERE p.status IN ('CAPTURED', 'RELEASED')")
    BigDecimal getAveragePaymentAmount();
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status IN ('CAPTURED', 'RELEASED') AND p.paymentCapturedAt >= :startDate AND p.paymentCapturedAt <= :endDate")
    BigDecimal getTotalPaymentVolumeBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // User earnings and spending
    @Query("SELECT SUM(p.netAmount) FROM Payment p WHERE p.payee = :user AND p.status = 'RELEASED'")
    BigDecimal getTotalEarningsForUser(@Param("user") User user);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.payer = :user AND p.status IN ('CAPTURED', 'RELEASED')")
    BigDecimal getTotalSpendingForUser(@Param("user") User user);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.payer = :user AND p.status IN ('CAPTURED', 'RELEASED')")
    Long countSuccessfulPaymentsByPayer(@Param("user") User user);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.payee = :user AND p.status = 'RELEASED'")
    Long countSuccessfulPaymentsByPayee(@Param("user") User user);
    
    // Payment method distribution
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p WHERE p.status IN ('CAPTURED', 'RELEASED') GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodDistribution();
    
    // Disputed payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'DISPUTED' ORDER BY p.createdAt DESC")
    List<Payment> findDisputedPayments();
    
    // Refunded payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'REFUNDED' ORDER BY p.refundedAt DESC")
    List<Payment> findRefundedPayments();
    
    @Query("SELECT p FROM Payment p WHERE p.payer = :user AND p.status = 'REFUNDED' ORDER BY p.refundedAt DESC")
    List<Payment> findRefundedPaymentsByPayer(@Param("user") User user);
    
    // Check if payment exists
    boolean existsByTask(Task task);
    
    boolean existsByTaskId(Long taskId);
    
    boolean existsByExternalPaymentId(String externalPaymentId);
}
