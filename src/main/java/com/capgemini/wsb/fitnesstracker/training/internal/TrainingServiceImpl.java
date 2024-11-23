package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.MonthlyTrainingReportDto;
import com.capgemini.wsb.fitnesstracker.mail.api.MonthlyAdminReportDto;
import com.capgemini.wsb.fitnesstracker.training.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public Optional<Training> getTraining(final Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    /**
     * Retrieves all trainings from the database.
     *
     * @return a list of all trainings.
     */
    public List<Training> findAllTrainings() {
        return trainingRepository.findAll();
    }

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of trainings associated with the user.
     */
    public List<Training> findTrainingsByUserId(Long userId) {
        return trainingRepository.findAllByUser_Id(userId); // Use the derived query method
    }

    /**
     * Retrieves all finished trainings after the specified time.
     *
     * @param afterTime the time after which trainings should be fetched.
     * @return a list of finished trainings.
     */
    public List<Training> findFinishedTrainingsAfter(Date afterTime) {
        return trainingRepository.findAllByEndTimeAfter(afterTime);
    }

    /**
     * Retrieves all trainings with the specified activity type.
     *
     * @param activityType the activity type to filter by.
     * @return a list of trainings with the given activity type.
     */
    public List<Training> findTrainingsByActivityType(ActivityType activityType) {
        return trainingRepository.findAllByActivityType(activityType);
    }

    /**
     * Creates a new training.
     *
     * @param request the DTO containing training details.
     * @return the saved Training entity.
     * @throws NoSuchElementException if the user is not found.
     */
    public Training createTraining(CreateTrainingRequestDto request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User with ID " + request.userId() + " not found"));

        Training training = new Training(
                user,
                request.startTime(),
                request.endTime(),
                request.activityType(),
                request.distance(),
                request.averageSpeed()
        );

        return trainingRepository.save(training);
    }

    /**
     * Updates an existing training.
     *
     * @param trainingId the ID of the training to update.
     * @param request the DTO containing updated training details.
     * @return the updated Training entity.
     * @throws NoSuchElementException if the training or user is not found.
     */
    public Training updateTraining(Long trainingId, UpdateTrainingRequestDto request) {
        Training existingTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new NoSuchElementException("Training with ID " + trainingId + " not found"));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User with ID " + request.userId() + " not found"));

        existingTraining.setUser(user);
        existingTraining.setStartTime(request.startTime());
        existingTraining.setEndTime(request.endTime());
        existingTraining.setActivityType(request.activityType());
        existingTraining.setDistance(request.distance());
        existingTraining.setAverageSpeed(request.averageSpeed());

        return trainingRepository.save(existingTraining);
    }

    /**
     * Generates a monthly report for a specific user.
     *
     * @param userId the ID of the user.
     * @param month the month for the report (e.g., "2024-10-01").
     * @return a MonthlyTrainingReportDto containing user and training details.
     */
    public MonthlyTrainingReportDto generateMonthlyReport(Long userId, LocalDate month) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());

        Date start = Timestamp.valueOf(startDate.atStartOfDay());
        Date end = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));

        List<Training> trainings = trainingRepository.findAllByUserIdAndMonth(userId, start, end);

        String userName = trainings.isEmpty() ? "" :
                trainings.get(0).getUser().getFirstName() + " " + trainings.get(0).getUser().getLastName();
        String userEmail = trainings.isEmpty() ? "" : trainings.get(0).getUser().getEmail();

        return new MonthlyTrainingReportDto(
                userId,
                userName,
                userEmail,
                trainings.stream().map(trainingMapper::toDto).toList()
        );
    }

    /**
     * Generates a monthly summary report for the administrator.
     *
     * @param month the month for which the report is generated.
     * @return a {@link MonthlyAdminReportDto} containing user summaries.
     */
    public MonthlyAdminReportDto generateAdminMonthlyReport(LocalDate month) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<MonthlyAdminReportDto.UserTrainingSummary> userSummaries = userRepository.findAll().stream()
                .map(user -> {
                    int trainingCount = trainingRepository.findAllByUserIdAndMonth(
                            user.getId(),
                            java.sql.Date.valueOf(startDate),
                            java.sql.Date.valueOf(endDate)
                    ).size();

                    return new MonthlyAdminReportDto.UserTrainingSummary(
                            user.getFirstName() + " " + user.getLastName(),
                            user.getEmail(),
                            trainingCount
                    );
                })
                .collect(Collectors.toList());

        return new MonthlyAdminReportDto(month.toString(), userSummaries);
    }
}
