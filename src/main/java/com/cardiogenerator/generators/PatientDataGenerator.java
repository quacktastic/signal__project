package com.cardiogenerator.generators;

import com.cardiogenerator.outputs.OutputStrategy;


/**
 * Defines the core abstraction contract for all health metric simulation components.
 * Provides a unified interface forcing concrete data generators to implement
 * specialized, time-bound vital sign generation routines for individual patients.
 */
public interface PatientDataGenerator {

    /**
     * Executes a single simulation iteration to compute and stream patient vital metrics.
     * Enforces how individual generators evaluate stochastic state adjustments and route the
     * resulting string event to the prepared output adapter.
     * @param patientId the unique numerical identifier representing the targeted patient.
     * @param outputStrategy the chosen data pipeline strategy used to print, save, or broadcast the event.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
