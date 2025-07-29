package com.example.mrquiz.service.notification;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmailService {

    public void sendQuizInvitation(String email, UUID quizId, String invitationToken, 
                                  Map<String, Object> customization) {
        // Implementation would send quiz invitation email
    }
}