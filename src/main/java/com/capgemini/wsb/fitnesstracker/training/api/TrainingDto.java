package com.capgemini.wsb.fitnesstracker.training.api;

import com.capgemini.wsb.fitnesstracker.user.internal.UserDto;

import java.util.Date;

/**
 * A Data Transfer Object for Training details.
 *
 * @param user the user associated with the training.
 * @param startTime the start time of the training.
 * @param endTime the end time of the training.
 * @param distance the distance covered during the training.
 * @param averageSpeed the average speed of the training.
 */
public record TrainingDto(
    UserDto user,
    Date startTime,
    Date endTime,
    double distance,
    double averageSpeed
) {}
