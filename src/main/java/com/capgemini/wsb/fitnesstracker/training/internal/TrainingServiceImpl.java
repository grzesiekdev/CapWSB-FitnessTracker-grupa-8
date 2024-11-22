package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;

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
}
