package com.capgemini.wsb.fitnesstracker.mail.api;

import java.util.List;

/**
 * DTO for the monthly admin training summary report.
 *
 * @param month the month of the report.
 * @param users a list of user training summaries.
 */
public record MonthlyAdminReportDto(
        String month,
        List<UserTrainingSummary> users
) {

    /**
     * Represents a summary of a single user's training activity.
     *
     * @param userName the full name of the user.
     * @param email the email address of the user.
     * @param trainingCount the number of trainings completed by the user.
     */
    public static record UserTrainingSummary(
            String userName,
            String email,
            int trainingCount
    ) {}
}
