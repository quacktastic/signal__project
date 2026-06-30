package com.cardiogenerator.generators;

import java.util.Random;

import com.cardiogenerator.outputs.OutputStrategy;

/**
 * Implements the patient data generator contract to simulate blood oxygen saturation ($SpO_2$) metrics.
 * This class maintains an internal state tracking array to preserve continuity across successive
 * time steps, simulating realistic localized fluctuations (random walks) bounded within clinical limits.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;


    /**
     * Constructs a blood saturation data generator and initializes baseline values for all simulated patients.
     * Generates a random initial baseline metric between 95% and 100% for each unique patient identifier.
     *
     * @param patientCount the total number of unique patients to initialize inside the state array
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }



    /**
     * Computes the next step in a continuous stochastic random walk to simulate blood saturation updates.
     * Applies localized delta variations, clamps the results to stable clinical boundaries (90% to 100%),
     * updates internal patient memory tracking, and streams the event row out to the chosen output adapter.
     *
     * @param patientId the unique numerical identifier representing the targeted patient
     * @param outputStrategy the chosen data pipeline strategy used to route or serialize the event
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
