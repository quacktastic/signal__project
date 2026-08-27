package com.datamanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {

    private final String outputDir;
    private final DataParser parser;

    public FileDataReader(String outputDir, DataParser parser) {
        this.outputDir = outputDir;
        this.parser = parser;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File target = new File(outputDir);

        if (!target.exists()) {
            throw new IOException("Provided file direction or file not found: " + outputDir);
        }

        if (target.isDirectory()) {
            File[] files = target.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        readFile(file, dataStorage);
                    }
                }
            }
        } else {
            readFile(target, dataStorage);
        }
    }


    // Read the file, called by readData if the specified file and path are valid
    public void readFile(File file, DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.parser.parseAndStore(line, dataStorage);
            }
        }
    }


}
