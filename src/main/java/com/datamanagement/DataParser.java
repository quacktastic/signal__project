package com.datamanagement;

public class DataParser {

    public void parseAndStore(String line, DataStorage dataStorage) {

        // Return empty
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        // comma
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return; // Skip the missing lines
        }

        try {
            int patientId = Integer.parseInt(extractValue(parts[0]));
            long timestamp = Long.parseLong(extractValue(parts[1]));
            String recordType = extractValue(parts[2]);
            String rawData = extractValue(parts[3]);

            double measurementValue;

            // ensure "triggered" do not create syntax errors etc. by assigning it as a double variable
            if (rawData.equalsIgnoreCase("triggered")) {
                measurementValue = 1.0;
            } else {
                String cleanedData = rawData.replace("%", "").trim();
                measurementValue = Double.parseDouble(cleanedData);
            }



            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (NumberFormatException e) {

            System.err.println("Raw couldn't be parsed: " + line);
        }
    }

    public String extractValue(String token) {
        if (token.contains(":")) {
            return token.substring(token.indexOf(":") + 1).trim();
        }
        return token.trim();
    }
}
