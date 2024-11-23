package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

interface TrainingRepository extends JpaRepository<Training, Long> {

    /**
     * Finds all trainings for a specific user by user ID.
     *
     * @param userId the ID of the user.
     * @return a list of trainings associated with the user.
     */
    List<Training> findAllByUser_Id(Long userId);

    /**
     * Finds all trainings that have finished (i.e., endTime is after the specified time).
     *
     * @param afterTime the time after which trainings should be fetched.
     * @return a list of finished trainings.
     */
    List<Training> findAllByEndTimeAfter(Date afterTime);

    /**
     * Finds all trainings with the specified activity type.
     *
     * @param activityType the type of activity to filter by.
     * @return a list of trainings with the given activity type.
     */
    List<Training> findAllByActivityType(ActivityType activityType);

    /**
     * Finds all trainings for a specific user in a specific month.
     *
     * @param userId the ID of the user.
     * @param startDate the start date of the month.
     * @param endDate the end date of the month.
     * @return a list of trainings for the user in the given month.
     */
    @Query("SELECT t FROM Training t WHERE t.user.id = :userId AND t.startTime BETWEEN :startDate AND :endDate")
    List<Training> findAllByUserIdAndMonth(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
