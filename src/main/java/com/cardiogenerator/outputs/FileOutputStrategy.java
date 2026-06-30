package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implements the output strategy contract to persist simulation data as structured text files.
 * This strategy creates individual text files named after specific vital metric labels,
 * routing patient record streams into distinct storage sinks on the local file system.
 */
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory;

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a file output strategy and initializes the target storage destination path.
     *
     * @param baseDirectory the root path string where the metric log files will be saved
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }


    /**
     * Writes a single stochastic event row to a specific metric text file on disk.
     * Automatically verifies target directory paths, caches file names dynamically to minimize
     * disk resolution overhead, and appends formatted metrics to the respective text asset.
     *
     * @param patientId the unique numerical identifier representing the simulated patient
     * @param timestamp the precise epoch time in milliseconds when the event occurred
     * @param label the descriptive category name of the health metric used to determine the target file
     * @param data the raw calculated value or status message generated for the metric
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}


