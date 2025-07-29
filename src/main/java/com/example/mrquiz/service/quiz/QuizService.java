package com.example.mrquiz.service.quiz;

import com.example.mrquiz.dto.quiz.*;
import com.example.mrquiz.entity.quiz.Quiz;
import com.example.mrquiz.entity.quiz.QuizQuestion;
import com.example.mrquiz.enums.QuizStatus;
import com.example.mrquiz.enums.QuizType;
import com.example.mrquiz.repository.quiz.QuizRepository;
import com.example.mrquiz.repository.quiz.QuizQuestionRepository;
import com.example.mrquiz.service.MappingService;
import com.example.mrquiz.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private QuestionService questionService;

    private final SecureRandom secureRandom = new SecureRandom();

    // ============================================================================
    // BASIC QUIZ OPERATIONS
    // ============================================================================

    public QuizResponseDto createQuiz(QuizCreateDto createDto) {
        Quiz quiz = mappingService.getQuizMapper().toEntity(createDto);
        quiz = quizRepository.save(quiz);
        return mappingService.getQuizMapper().toResponseDto(quiz);
    }

    public QuizResponseDto updateQuiz(UUID quizId, QuizUpdateDto updateDto) {
        Quiz quiz = findQuizById(quizId);
        mappingService.getQuizMapper().updateEntity(updateDto, quiz);
        quiz = quizRepository.save(quiz);
        return mappingService.getQuizMapper().toResponseDto(quiz);
    }

    public QuizResponseDto getQuizById(UUID quizId) {
        Quiz quiz = findQuizById(quizId);
        return mappingService.getQuizMapper().toResponseDto(quiz);
    }

    public void deleteQuiz(UUID quizId) {
        Quiz quiz = findQuizById(quizId);
        quiz.setStatus(QuizStatus.ARCHIVED);
        quizRepository.save(quiz);
    }

    // ============================================================================
    // TEACHER QUIZ MANAGEMENT
    // ============================================================================

    public Page<QuizResponseDto> getTeacherQuizzes(UUID teacherId, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByCreatorId(teacherId, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    public Page<QuizResponseDto> getTeacherQuizzesByStatus(UUID teacherId, QuizStatus status, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByCreatorIdAndStatus(teacherId, status, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    public List<QuizResponseDto> getTeacherPublishedQuizzes(UUID teacherId) {
        List<Quiz> quizzes = quizRepository.findPublishedQuizzesByCreator(teacherId);
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public List<QuizResponseDto> getTeacherDraftQuizzes(UUID teacherId) {
        List<Quiz> quizzes = quizRepository.findDraftQuizzesByCreator(teacherId);
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public long countTeacherQuizzes(UUID teacherId) {
        return quizRepository.countByCreatorId(teacherId);
    }

    // ============================================================================
    // QUIZ DISCOVERY AND ACCESS
    // ============================================================================

    public Page<QuizResponseDto> getAvailableQuizzes(UUID userId, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findAvailableQuizzes(userId, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    public List<QuizResponseDto> getPublicQuizzes() {
        List<Quiz> quizzes = quizRepository.findPublicQuizzes();
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public Page<QuizResponseDto> getCourseQuizzes(UUID courseId, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByCourseId(courseId, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    public Page<QuizResponseDto> getInstitutionQuizzes(UUID institutionId, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByInstitutionId(institutionId, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    // ============================================================================
    // SEARCH AND FILTERING
    // ============================================================================

    public Page<QuizResponseDto> searchQuizzes(String searchTerm, QuizType type, 
                                              UUID institutionId, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.searchQuizzes(searchTerm, type, institutionId, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    public Page<QuizResponseDto> searchQuizzesByTags(List<String> tags, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByTags(tags, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    public Page<QuizResponseDto> getQuizzesByDifficulty(String difficulty, Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByDifficulty(difficulty, pageable);
        return mappingService.getQuizMapper().toResponseDtoPage(quizzes);
    }

    // ============================================================================
    // QUIZ TEMPLATES AND SHARING
    // ============================================================================

    public List<QuizResponseDto> getQuizTemplates() {
        List<Quiz> templates = quizRepository.findTemplates();
        return mappingService.getQuizMapper().toResponseDtoList(templates);
    }

    public List<QuizResponseDto> getShareableQuizzes(UUID userId) {
        List<Quiz> quizzes = quizRepository.findShareableQuizzes(userId);
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public List<QuizResponseDto> getFeaturedQuizzes() {
        List<Quiz> quizzes = quizRepository.findFeaturedQuizzes();
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public QuizResponseDto createQuizFromTemplate(UUID templateId, UUID creatorId) {
        Quiz template = findQuizById(templateId);
        
        QuizCreateDto createDto = new QuizCreateDto();
        createDto.setCreatorId(creatorId);
        createDto.setTitle(template.getTitle() + " - Copy");
        createDto.setDescription(template.getDescription());
        createDto.setQuizType(template.getQuizType());
        createDto.setTimeLimit(template.getTimeLimit());
        createDto.setAttemptsAllowed(template.getAttemptsAllowed());
        createDto.setSettings(template.getSettings());
        
        Quiz newQuiz = mappingService.getQuizMapper().toEntity(createDto);
        newQuiz = quizRepository.save(newQuiz);
        
        // Copy questions from template
        copyQuizQuestions(templateId, newQuiz.getId());
        
        return mappingService.getQuizMapper().toResponseDto(newQuiz);
    }

    // ============================================================================
    // SCHEDULING AND AVAILABILITY
    // ============================================================================

    public List<QuizResponseDto> getScheduledQuizzes() {
        List<Quiz> quizzes = quizRepository.findScheduledQuizzes();
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public List<QuizResponseDto> getQuizzesStartingSoon(int hours) {
        LocalDateTime threshold = LocalDateTime.now().plusHours(hours);
        List<Quiz> quizzes = quizRepository.findQuizzesStartingSoon(threshold);
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public List<QuizResponseDto> getExpiredQuizzes() {
        List<Quiz> quizzes = quizRepository.findExpiredQuizzes();
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public void scheduleQuiz(UUID quizId, LocalDateTime startTime, LocalDateTime endTime) {
        Quiz quiz = findQuizById(quizId);
        quiz.setAvailabilityStart(startTime);
        quiz.setAvailabilityEnd(endTime);
        quiz.setStatus(QuizStatus.SCHEDULED);
        quizRepository.save(quiz);
    }

    // ============================================================================
    // ANALYTICS SUPPORT
    // ============================================================================

    public List<QuizResponseDto> getMostPopularQuizzes(int limit) {
        List<Quiz> quizzes = quizRepository.findMostPopularQuizzes(limit);
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public List<QuizResponseDto> getHighestRatedQuizzes(int limit) {
        List<Quiz> quizzes = quizRepository.findHighestRatedQuizzes(limit);
        return mappingService.getQuizMapper().toResponseDtoList(quizzes);
    }

    public Map<String, Object> getQuizPerformanceSummary(UUID quizId) {
        return quizRepository.getQuizPerformanceSummary(quizId).stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> arr[1]
                ));
    }

    // ============================================================================
    // BULK OPERATIONS
    // ============================================================================

    public void bulkUpdateQuizStatus(List<UUID> quizIds, QuizStatus status) {
        quizRepository.bulkUpdateStatus(quizIds, status);
    }

    public void bulkUpdateTotalPoints(List<UUID> quizIds) {
        for (UUID quizId : quizIds) {
            updateQuizTotalPoints(quizId);
        }
    }

    public void bulkArchiveQuizzes(List<UUID> quizIds) {
        bulkUpdateQuizStatus(quizIds, QuizStatus.ARCHIVED);
    }

    // ============================================================================
    // COLLABORATION AND VERSION CONTROL
    // ============================================================================

    public void shareQuizWithTeacher(UUID quizId, UUID teacherId, String permission) {
        Quiz quiz = findQuizById(quizId);
        Map<String, Object> settings = quiz.getSettings();
        
        @SuppressWarnings("unchecked")
        Map<String, String> collaborators = (Map<String, String>) settings.getOrDefault("collaborators", new HashMap<>());
        collaborators.put(teacherId.toString(), permission);
        settings.put("collaborators", collaborators);
        
        quiz.setSettings(settings);
        quizRepository.save(quiz);
        
        // Send collaboration notification
        notificationService.sendCollaborationInvitation(teacherId, quizId);
    }

    public void createQuizVersion(UUID quizId, String versionNote) {
        Quiz quiz = findQuizById(quizId);
        Map<String, Object> settings = quiz.getSettings();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> versions = (List<Map<String, Object>>) settings.getOrDefault("versions", new ArrayList<>());
        
        Map<String, Object> version = new HashMap<>();
        version.put("timestamp", LocalDateTime.now());
        version.put("note", versionNote);
        version.put("snapshot", createQuizSnapshot(quiz));
        
        versions.add(version);
        settings.put("versions", versions);
        
        quiz.setSettings(settings);
        quizRepository.save(quiz);
    }

    // ============================================================================
    // STUDENT ACCESS AND INVITATION
    // ============================================================================

    public String generateJoinCode(UUID quizId) {
        Quiz quiz = findQuizById(quizId);
        String joinCode = generateAlphanumericCode(8);
        
        Map<String, Object> settings = quiz.getSettings();
        settings.put("joinCode", joinCode);
        settings.put("joinCodeGenerated", LocalDateTime.now());
        
        quiz.setSettings(settings);
        quizRepository.save(quiz);
        
        return joinCode;
    }

    public Optional<QuizResponseDto> getQuizByJoinCode(String joinCode) {
        Optional<Quiz> quiz = quizRepository.findByJoinCode(joinCode);
        return quiz.map(q -> mappingService.getQuizMapper().toResponseDto(q));
    }

    public void enableGuestAccess(UUID quizId, boolean allowGuests) {
        Quiz quiz = findQuizById(quizId);
        Map<String, Object> settings = quiz.getSettings();
        settings.put("allowGuestAccess", allowGuests);
        quiz.setSettings(settings);
        quizRepository.save(quiz);
    }

    // ============================================================================
    // ADVANCED QUIZ FEATURES
    // ============================================================================

    public void enableAdaptiveMode(UUID quizId, Map<String, Object> adaptiveSettings) {
        Quiz quiz = findQuizById(quizId);
        Map<String, Object> settings = quiz.getSettings();
        settings.put("adaptiveMode", true);
        settings.put("adaptiveSettings", adaptiveSettings);
        quiz.setSettings(settings);
        quizRepository.save(quiz);
    }

    public void enableProctoringMode(UUID quizId, Map<String, Object> proctoringSettings) {
        Quiz quiz = findQuizById(quizId);
        quiz.setProctoringSettings(proctoringSettings);
        quizRepository.save(quiz);
    }

    public void setTimedMode(UUID quizId, boolean timed, Integer timeLimit) {
        Quiz quiz = findQuizById(quizId);
        quiz.setTimeLimit(timed ? timeLimit : null);
        
        Map<String, Object> settings = quiz.getSettings();
        settings.put("timedMode", timed);
        quiz.setSettings(settings);
        
        quizRepository.save(quiz);
    }

    // ============================================================================
    // STATISTICS AND REPORTING
    // ============================================================================

    public Map<String, Long> getQuizCountByType() {
        return quizRepository.countQuizzesByType().stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Long> getQuizCountByStatus() {
        return quizRepository.countQuizzesByStatus().stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Long> getQuizCreationStats(LocalDateTime start, LocalDateTime end) {
        return quizRepository.getQuizCreationStats(start, end).stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Object> getTeacherProductivityStats(UUID teacherId) {
        return quizRepository.getTeacherProductivityStats(teacherId).stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> arr[1]
                ));
    }

    // ============================================================================
    // QUIZ QUESTION MANAGEMENT
    // ============================================================================

    public void addQuestionToQuiz(UUID quizId, UUID questionId, int orderIndex) {
        Quiz quiz = findQuizById(quizId);
        
        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setQuiz(quiz);
        quizQuestion.setQuestion(questionService.findQuestionEntityById(questionId));
        quizQuestion.setOrderIndex(orderIndex);
        
        quizQuestionRepository.save(quizQuestion);
        
        // Update quiz total points
        updateQuizTotalPoints(quizId);
    }

    public void removeQuestionFromQuiz(UUID quizId, UUID questionId) {
        quizQuestionRepository.deleteByQuizIdAndQuestionId(quizId, questionId);
        updateQuizTotalPoints(quizId);
    }

    public void reorderQuizQuestions(UUID quizId, List<UUID> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            quizQuestionRepository.updateQuestionOrder(quizId, questionIds.get(i), i + 1);
        }
    }

    private void copyQuizQuestions(UUID sourceQuizId, UUID targetQuizId) {
        List<QuizQuestion> sourceQuestions = quizQuestionRepository.findByQuizIdOrderByOrderIndex(sourceQuizId);
        Quiz targetQuiz = findQuizById(targetQuizId);
        
        for (QuizQuestion sourceQuestion : sourceQuestions) {
            QuizQuestion newQuizQuestion = new QuizQuestion();
            newQuizQuestion.setQuiz(targetQuiz);
            newQuizQuestion.setQuestion(sourceQuestion.getQuestion());
            newQuizQuestion.setOrderIndex(sourceQuestion.getOrderIndex());
            newQuizQuestion.setPoints(sourceQuestion.getPoints());
            newQuizQuestion.setTimeLimit(sourceQuestion.getTimeLimit());
            newQuizQuestion.setRequired(sourceQuestion.getRequired());
            newQuizQuestion.setSettings(sourceQuestion.getSettings());
            
            quizQuestionRepository.save(newQuizQuestion);
        }
        
        updateQuizTotalPoints(targetQuizId);
    }

    private void updateQuizTotalPoints(UUID quizId) {
        BigDecimal totalPoints = quizQuestionRepository.calculateTotalPoints(quizId);
        quizRepository.updateTotalPoints(quizId, totalPoints);
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private Quiz findQuizById(UUID quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    private String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return code.toString();
    }

    private Map<String, Object> createQuizSnapshot(Quiz quiz) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("title", quiz.getTitle());
        snapshot.put("description", quiz.getDescription());
        snapshot.put("settings", quiz.getSettings());
        snapshot.put("timeLimit", quiz.getTimeLimit());
        snapshot.put("totalPoints", quiz.getTotalPoints());
        return snapshot;
    }

    public boolean hasAccessToQuiz(UUID userId, UUID quizId) {
        Quiz quiz = findQuizById(quizId);
        
        // Creator has full access
        if (quiz.getCreator().getId().equals(userId)) {
            return true;
        }
        
        // Check collaborator access
        Map<String, Object> settings = quiz.getSettings();
        @SuppressWarnings("unchecked")
        Map<String, String> collaborators = (Map<String, String>) settings.get("collaborators");
        if (collaborators != null && collaborators.containsKey(userId.toString())) {
            return true;
        }
        
        // Check public access or guest access
        return quiz.getStatus() == QuizStatus.PUBLISHED && 
               (Boolean) settings.getOrDefault("allowGuestAccess", false);
    }
}