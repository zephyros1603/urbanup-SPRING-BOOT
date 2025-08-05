package com.urbanup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.urbanup.repository.UserRepository;
import com.urbanup.repository.TaskRepository;
import com.urbanup.repository.ReviewRepository;
import com.urbanup.repository.PaymentRepository;
import com.urbanup.repository.NotificationRepository;
import com.urbanup.repository.FileUploadRepository;
import com.urbanup.repository.KycSubmissionRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private KycSubmissionRepository kycSubmissionRepository;

    // Add methods for admin functionalities here
}