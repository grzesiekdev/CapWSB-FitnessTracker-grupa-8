package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.MonthlyTrainingReportDto;
import com.capgemini.wsb.fitnesstracker.training.internal.TrainingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for managing email-related operations, including sending test emails and monthly training reports.
 */
@RestController
@RequestMapping("/v1/emails")
@RequiredArgsConstructor
public class EmailController {

    private final TrainingServiceImpl trainingService;
    private final EmailService emailService;

    /**
     * Sends a test email to the specified recipient.
     *
     * <p>This endpoint is primarily used for testing email functionality. It allows specifying the recipient,
     * subject, and content of the email to verify that the email-sending mechanism is operational.</p>
     *
     * @param to      the email address of the recipient.
     * @param subject the subject of the email.
     * @param content the content/body of the email.
     * @return a {@link ResponseEntity} with an HTTP 200 status and a confirmation message if the email was sent successfully.
     */
    @PostMapping("/send-test")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to,
                                                @RequestParam String subject,
                                                @RequestParam String content) {
        emailService.sendCustomEmail(to, subject, content);
        return ResponseEntity.ok("Email sent successfully to " + to);
    }

    /**
     * Sends a monthly training report email for the specified user and month.
     *
     * <p>This endpoint generates a training report for the given user and month, including a summary of their
     * training activities during that month. The report is emailed to the user's email address as specified in their profile.</p>
     *
     * @param userId the ID of the user for whom the report is generated.
     * @param month  the month for which the report is generated, in the format "YYYY-MM" (e.g., "2024-10").
     * @return a {@link ResponseEntity}:
     * <ul>
     *     <li>HTTP 200 (OK) if the report is successfully generated and emailed.</li>
     *     <li>HTTP 400 (Bad Request) if no trainings are found for the given user and month.</li>
     *     <li>HTTP 500 (Internal Server Error) if an unexpected error occurs.</li>
     * </ul>
     */
    @PostMapping("/monthly-report/{userId}")
    public ResponseEntity<String> sendMonthlyReport(@PathVariable Long userId, @RequestParam String month) {
        try {
            LocalDate reportMonth = LocalDate.parse(month + "-01");

            MonthlyTrainingReportDto report = trainingService.generateMonthlyReport(userId, reportMonth);

            if (report.trainings().isEmpty()) {
                return ResponseEntity.badRequest().body("No trainings found for the given month.");
            }

            emailService.sendMonthlyReport(report);
            return ResponseEntity.ok("Monthly report sent to " + report.userEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send the report: " + e.getMessage());
        }
    }
}
