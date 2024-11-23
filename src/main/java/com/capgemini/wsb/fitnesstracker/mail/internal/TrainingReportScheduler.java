package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.MonthlyTrainingReportDto;
import com.capgemini.wsb.fitnesstracker.training.internal.TrainingServiceImpl;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler for generating and sending monthly training reports.
 *
 * <p>This component automatically triggers on a scheduled basis to collect training data for
 * all users from the previous month and send a summary email to each user.</p>
 */
@Component
@RequiredArgsConstructor
public class TrainingReportScheduler {

    private final UserRepository userRepository;
    private final TrainingServiceImpl trainingService;
    private final EmailService emailService;

    /**
     * Generates and sends monthly training reports.
     *
     * <p>This method runs on the 1st day of each month at 8:00 AM (server time) and performs the following steps:</p>
     * <ul>
     *     <li>Retrieves the list of all users from the {@link UserRepository}.</li>
     *     <li>For each user, generates a training report for the previous month using the {@link TrainingServiceImpl}.</li>
     *     <li>If the report contains trainings, sends the report to the user's email via the {@link EmailService}.</li>
     * </ul>
     *
     * <p>The scheduling is configured via the {@code @Scheduled} annotation with a CRON expression.</p>
     *
     * @see UserRepository
     * @see TrainingServiceImpl
     * @see EmailService
     */
    @Scheduled(cron = "0 0 8 1 * *")
    public void generateAndSendMonthlyReports() {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);

        List<User> users = userRepository.findAll();

        for (User user : users) {
            MonthlyTrainingReportDto report = trainingService.generateMonthlyReport(user.getId(), previousMonth);

            if (!report.trainings().isEmpty()) {
                emailService.sendMonthlyReport(report);
            }
        }
    }
}
