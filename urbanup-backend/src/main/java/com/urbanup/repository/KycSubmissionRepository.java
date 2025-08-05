package com.urbanup.repository;

import com.urbanup.entity.KycSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycSubmissionRepository extends JpaRepository<KycSubmission, Long> {
    // Additional query methods can be defined here if needed
}