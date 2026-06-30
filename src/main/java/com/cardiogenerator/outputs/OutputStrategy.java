package com.cardiogenerator.outputs;


/**
 * Defines the core abstraction contract for all data transmission and storage strategies.
 * This interface decouples the data generation layer from specific output destinations,
 * forcing all concrete handlers to implement a uniform data routing routine.
 */
public interface OutputStrategy {

    /**
     * Dispatches a single simulated medical event to the designated output destination.
     * Enforces the structural formatting contract required to process and log a patient's
     * real-time stochastic vital sign entry.
     *
     * @param patientId the unique numerical identifier representing the simulated patient
     * @param timestamp the precise epoch time in milliseconds when the event was recorded
     * @param label the descriptive category name of the health metric (e.g., "ECG", "Saturation")
     * @param data the raw calculated value or status message generated for the metric
     */
    void output(int patientId, long timestamp, String label, String data);
}
