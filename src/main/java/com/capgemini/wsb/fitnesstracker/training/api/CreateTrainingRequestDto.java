package com.capgemini.wsb.fitnesstracker.training.api;

import com.capgemini.wsb.fitnesstracker.training.internal.ActivityType;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/**
 * DTO for creating a new training.
 */
public record CreateTrainingRequestDto(
        @NotNull Long userId,
        @NotNull Date startTime,
        @NotNull Date endTime,
        @NotNull ActivityType activityType,
        @NotNull double distance,
        @NotNull double averageSpeed
) {}
