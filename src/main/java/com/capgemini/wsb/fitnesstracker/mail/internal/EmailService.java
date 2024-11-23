package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailDto;
import com.capgemini.wsb.fitnesstracker.mail.api.EmailSender;
import com.capgemini.wsb.fitnesstracker.mail.api.MonthlyAdminReportDto;
import com.capgemini.wsb.fitnesstracker.mail.api.MonthlyTrainingReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling email operations, including sending custom emails and monthly training reports.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailSender emailSender;

    /**
     * Sends a custom email message.
     *
     * <p>This method allows sending an email with arbitrary content, subject, and recipient address. It constructs
     * an {@link EmailDto} object and uses the {@link EmailSender} implementation to send the email.</p>
     *
     * @param to      the recipient's email address.
     * @param subject the subject of the email.
     * @param content the content/body of the email.
     */
    public void sendCustomEmail(String to, String subject, String content) {
        EmailDto email = new EmailDto(to, subject, content);
        emailSender.send(email);
    }

    /**
     * Sends a monthly training report email to the specified user.
     *
     * <p>This method formats a detailed training report from the provided {@link MonthlyTrainingReportDto} and
     * sends it to the user's email address. If the report contains no trainings, the method exits without sending
     * an email.</p>
     *
     * @param report the monthly training report DTO containing user details and their trainings.
     */
    public void sendMonthlyReport(MonthlyTrainingReportDto report) {
        if (report.trainings().isEmpty()) return;

        String content = formatTrainingReport(report);

        EmailDto email = new EmailDto(report.userEmail(), "Monthly Training Summary", content);
        emailSender.send(email);
    }

    /**
     * Formats the training report content into a user-friendly email body.
     *
     * <p>This helper method creates a textual summary of the user's training activities for the month, including
     * details such as activity type, start time, end time, distance, and average speed.</p>
     *
     * @param report the monthly training report DTO containing user and training details.
     * @return the formatted email content as a {@link String}.
     */
    private String formatTrainingReport(MonthlyTrainingReportDto report) {
        StringBuilder content = new StringBuilder();
        content.append("Dear ").append(report.userName()).append(",\n\n");
        content.append("Your training summary for the month:\n\n");

        report.trainings().forEach(training -> {
            content.append("Activity: ").append(training.activityType()).append("\n");
            content.append("Start Time: ").append(training.startTime()).append("\n");
            content.append("End Time: ").append(training.endTime()).append("\n");
            content.append("Distance: ").append(training.distance()).append(" km\n");
            content.append("Average Speed: ").append(training.averageSpeed()).append(" km/h\n\n");
        });

        return content.toString();
    }

    /**
     * Sends a monthly admin report email.
     *
     * @param adminEmail the administrator's email address.
     * @param report     the monthly admin training summary report.
     */
    public void sendAdminMonthlyReport(String adminEmail, MonthlyAdminReportDto report) {
        String content = formatAdminReport(report);
        EmailDto email = new EmailDto(adminEmail, "Monthly Training Summary (Admin)", content);
        emailSender.send(email);
    }

    /**
     * Formats the admin report into a user-friendly email body.
     *
     * @param report the admin training summary report.
     * @return the formatted email content.
     */
    private String formatAdminReport(MonthlyAdminReportDto report) {
        StringBuilder content = new StringBuilder();
        content.append("Dear Administrator,\n\n");
        content.append("Here is the training summary for ").append(report.month()).append(":\n\n");

        report.users().forEach(user -> {
            content.append("User: ").append(user.userName()).append("\n");
            content.append("Email: ").append(user.email()).append("\n");
            content.append("Trainings Completed: ").append(user.trainingCount()).append("\n\n");
        });

        content.append("Best regards,\nYour Fitness Tracker Team");
        return content.toString();
    }

}
