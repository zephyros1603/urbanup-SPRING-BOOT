package com.urbanup.repository;

import com.urbanup.entity.TaskApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskApplicationRepository extends JpaRepository<TaskApplication, Long> {
    // Additional query methods can be defined here if needed
}