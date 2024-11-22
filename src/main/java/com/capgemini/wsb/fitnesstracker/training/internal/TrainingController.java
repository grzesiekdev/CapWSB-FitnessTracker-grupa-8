package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.TrainingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingServiceImpl trainingService;
    private final TrainingMapper trainingMapper;

    /**
     * Retrieves all trainings with detailed information.
     *
     * @return a list of TrainingDto objects.
     */
    @GetMapping
    public List<TrainingDto> getAllTrainings() {
        return trainingService.findAllTrainings()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of TrainingDto objects associated with the user.
     */
    @GetMapping("/{userId}")
    public List<TrainingDto> getTrainingsByUserId(@PathVariable Long userId) {
        return trainingService.findTrainingsByUserId(userId)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all finished trainings that ended after the specified time.
     *
     * @param afterTime the time after which finished trainings should be fetched.
     * @return a list of TrainingDto objects.
     */
    @GetMapping("/finished/{afterTime}")
    public List<TrainingDto> getFinishedTrainingsAfter(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date afterTime) {
        return trainingService.findFinishedTrainingsAfter(afterTime)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all trainings with the specified activity type.
     *
     * @param activityType the activity type to filter by.
     * @return a list of TrainingDto objects matching the activity type.
     */
    @GetMapping("/activityType")
    public List<TrainingDto> getTrainingsByActivityType(@RequestParam ActivityType activityType) {
        return trainingService.findTrainingsByActivityType(activityType)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }
}
