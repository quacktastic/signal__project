package com.cardiogenerator.generators;

import java.util.Random;

import com.cardiogenerator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    public static final Random RANDOM_GENERATOR = new Random();
    private boolean[] alertStates; // false = resolved, true = pressed


    /**
     * Implements the patient data generator contract to simulate emergency medical alert events.
     * This class operates as a stochastic state machine tracking active alert statuses per patient,
     * utilizing exponential distribution probabilities to trigger alerts and high fixed probabilities
     * to simulate their resolution.
     */


    /**
     * Constructs an alert generator and initializes empty/stable tracking histories for all patients.
     *
     * @param patientCount the total number of unique patients to initialize within the state array
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }


    /**
     * Evaluates and updates the state machine transitions to simulate sudden clinical alert dynamics.
     * If an alert is active, it evaluates a recovery path. If stable, it leverages a Poisson-rate
     * probability threshold calculation to determine if an emergency situation is triggered.
     *
     * @param patientId the unique numerical identifier representing the targeted patient
     * @param outputStrategy the chosen data pipeline strategy used to route or serialize the event
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
