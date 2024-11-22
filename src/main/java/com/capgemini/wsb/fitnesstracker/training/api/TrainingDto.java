package com.capgemini.wsb.fitnesstracker.training.api;

import com.capgemini.wsb.fitnesstracker.training.internal.ActivityType;
import com.capgemini.wsb.fitnesstracker.user.internal.UserDto;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/**
 * A Data Transfer Object for Training details.
 *
 * @param user the user associated with the training.
 * @param startTime the start time of the training.
 * @param endTime the end time of the training.
 * @param activityType the type of activity performed during the training.
 * @param distance the distance covered during the training.
 * @param averageSpeed the average speed of the training.
 */
public record TrainingDto(
        UserDto user,
        Date startTime,
        Date endTime,
        ActivityType activityType,
        double distance,
        double averageSpeed
) {}
