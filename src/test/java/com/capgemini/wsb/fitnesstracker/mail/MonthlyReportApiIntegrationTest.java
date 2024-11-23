package com.capgemini.wsb.fitnesstracker.mail;

import com.capgemini.wsb.fitnesstracker.IntegrationTest;
import com.capgemini.wsb.fitnesstracker.IntegrationTestBase;
import com.capgemini.wsb.fitnesstracker.mail.internal.EmailService;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.internal.ActivityType;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class MonthlyReportApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendMonthlyReport_whenTrainingsExist() throws Exception {
        // Given
        User user1 = existingUser(generateClient());
        persistTraining(generateTrainingWithDetails(user1, "2024-10-01 08:00:00", "2024-10-01 09:30:00", ActivityType.RUNNING, 10.5, 8.2));
        persistTraining(generateTrainingWithDetails(user1, "2024-10-15 18:00:00", "2024-10-15 19:30:00", ActivityType.CYCLING, 15.0, 12.0));

        // When & Then
        mockMvc.perform(post("/v1/emails/monthly-report/{userId}", user1.getId())
                        .param("month", "2024-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequest_whenNoTrainingsExist() throws Exception {
        // Given
        User user1 = existingUser(generateClient());

        // When & Then
        mockMvc.perform(post("/v1/emails/monthly-report/{userId}", user1.getId())
                        .param("month", "2024-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSendAdminMonthlyReport_whenTrainingsExist() throws Exception {
        // Given
        User user1 = existingUser(generateClient());
        User user2 = existingUser(generateClient());
        persistTraining(generateTrainingWithDetails(user1, "2024-10-01 08:00:00", "2024-10-01 09:30:00", ActivityType.RUNNING, 10.5, 8.2));
        persistTraining(generateTrainingWithDetails(user2, "2024-10-15 18:00:00", "2024-10-15 19:30:00", ActivityType.CYCLING, 15.0, 12.0));
        persistTraining(generateTrainingWithDetails(user2, "2024-10-20 10:00:00", "2024-10-20 11:30:00", ActivityType.TENNIS, 5.0, 4.0));

        // When & Then
        mockMvc.perform(post("/v1/emails/monthly-admin-report")
                        .param("adminEmail", "admin@example.com")
                        .param("month", "2024-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOk_whenNoTrainingsExistForAnyUser() throws Exception {
        // Given
        User user1 = existingUser(generateClient());
        User user2 = existingUser(generateClient());

        // When & Then
        mockMvc.perform(post("/v1/emails/monthly-admin-report")
                        .param("adminEmail", "admin@example.com")
                        .param("month", "2024-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk());
    }

    private static User generateClient() {
        return new User(randomUUID().toString(), randomUUID().toString(), now(), randomUUID().toString());
    }

    private static Training generateTrainingWithDetails(User user, String startTime, String endTime, ActivityType activityType, double distance, double averageSpeed) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return new Training(
                user,
                sdf.parse(startTime),
                sdf.parse(endTime),
                activityType,
                distance,
                averageSpeed);
    }
}
