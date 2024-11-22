package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
