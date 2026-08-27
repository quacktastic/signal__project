package data_management;


import static org.junit.jupiter.api.Assertions.*;

import com.datamanagement.DataParser;
import com.datamanagement.DataStorage;
import com.datamanagement.FileDataReader;
import com.datamanagement.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileDataReaderTest {

    private DataStorage dataStorage;
    private DataParser dataParser;

    @BeforeEach
    void setUp() {
        dataStorage = new DataStorage();
        dataParser = new DataParser();
    }

    @Test
    void testReadDataFromDirectoryWithMultipleFiles(@TempDir Path tempDir) throws IOException {
        File file1 = tempDir.resolve("patient_data_1.txt").toFile();
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("1, 1714376789050, SystolicPressure, 120.0\n");
            writer.write("1, 1714376789051, DiastolicPressure, 80.0\n");
        }

        File file2 = tempDir.resolve("patient_data_2.txt").toFile();
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("2, 1714376789052, Saturation, 98.0%\n");
        }

        FileDataReader reader = new FileDataReader(tempDir.toString(), dataParser);
        reader.readData(dataStorage);

        List<PatientRecord> recordsPatient1 = dataStorage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, recordsPatient1.size(), "2 records required for the first patient.");
        assertEquals(120.0, recordsPatient1.get(0).getMeasurementValue());
        assertEquals(80.0, recordsPatient1.get(1).getMeasurementValue());

        // 4. Hasta 2 kayıtlarını doğrula
        List<PatientRecord> recordsPatient2 = dataStorage.getRecords(2, 1714376789052L, 1714376789052L);
        assertEquals(1, recordsPatient2.size(), "1 record required for the second patient.");
        assertEquals(98.0, recordsPatient2.get(0).getMeasurementValue());
    }

    @Test
    void testReadDataFromSingleFile(@TempDir Path tempDir) throws IOException {
        File singleFile = tempDir.resolve("single_record.txt").toFile();
        try (FileWriter writer = new FileWriter(singleFile)) {
            writer.write("Patient ID: 5, Timestamp: 1714376789100, Label: Cholesterol, Data: 190.5\n");
        }

        FileDataReader reader = new FileDataReader(singleFile.getAbsolutePath(), dataParser);
        reader.readData(dataStorage);

        List<PatientRecord> records = dataStorage.getRecords(5, 1714376789100L, 1714376789100L);
        assertEquals(1, records.size(), "1 record must be read from a single.");
        assertEquals(190.5, records.get(0).getMeasurementValue());
    }

    @Test
    void testReadDataEmptyDirectory(@TempDir Path tempDir) throws IOException {
        FileDataReader reader = new FileDataReader(tempDir.toString(), dataParser);
        assertDoesNotThrow(() -> reader.readData(dataStorage));

        List<PatientRecord> records = dataStorage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(0, records.size());
    }

    @Test
    void testReadDataNonExistentPath() {
        FileDataReader reader = new FileDataReader("invalid path", dataParser);
        assertThrows(IOException.class, () -> reader.readData(dataStorage), "IOException required for an invalid path.");
    }
}
