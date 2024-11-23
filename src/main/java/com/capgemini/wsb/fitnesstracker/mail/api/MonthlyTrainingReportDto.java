package com.capgemini.wsb.fitnesstracker.mail.api;

import com.capgemini.wsb.fitnesstracker.training.api.TrainingDto;

import java.util.List;

/**
 * A Data Transfer Object for monthly training reports.
 */
public record MonthlyTrainingReportDto(
        Long userId,
        String userName,
        String userEmail,
        List<TrainingDto> trainings
) {}
