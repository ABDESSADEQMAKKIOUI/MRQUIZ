package com.example.mrquiz.service.notification;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {

    public void sendTeacherVerificationNotification(UUID teacherId) {
        // Implementation would send verification notification
    }

    public void sendCollaborationInvitation(UUID teacherId, UUID quizId) {
        // Implementation would send collaboration invitation
    }
}