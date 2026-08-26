package com.cardiogenerator.outputs;

public class ConsoleOutputStrategy implements OutputStrategy {
    // int x = 0;
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
     //   System.out.print(x++ + " -> ");
        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
    }
}
