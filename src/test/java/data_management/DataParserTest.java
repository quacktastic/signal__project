package data_management;

import com.datamanagement.DataParser;
import com.datamanagement.DataStorage;
import com.datamanagement.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataParserTest {

    private DataStorage dataStorage;
    private DataParser dataParser;

    @BeforeEach
    void setUp() {
        dataStorage = new DataStorage();
        dataParser = new DataParser();
    }

    @Test
    void testParseStandardCsvLine() {
        String line = "1, 1714376789050, SystolicPressure, 120.0";
        dataParser.parseAndStore(line, dataStorage);

        List<PatientRecord> records = dataStorage.getRecords(1, 1714376789050L, 1714376789050L);
        assertEquals(1, records.size(), "One record must be included.");
        assertEquals(120.0, records.get(0).getMeasurementValue());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(1714376789050L, records.get(0).getTimestamp());
    }

    @Test
    void testParseLabeledFormatWithPercentage() {
        String line = "Patient ID: 2, Timestamp: 1714376789100, Label: Saturation, Data: 98.0%";
        dataParser.parseAndStore(line, dataStorage);

        List<PatientRecord> records = dataStorage.getRecords(2, 1714376789100L, 1714376789100L);
        assertEquals(1, records.size());
        assertEquals(98.0, records.get(0).getMeasurementValue(), "'%' symbol should be eliminated and must return 98.0 only.");
        assertEquals("Saturation", records.get(0).getRecordType());
    }

    @Test
    void testParseTriggeredAlert() {
        String line = "Patient ID: 3, Timestamp: 1714376789200, Label: Alert, Data: triggered";
        dataParser.parseAndStore(line, dataStorage);

        List<PatientRecord> records = dataStorage.getRecords(3, 1714376789200L, 1714376789200L);
        assertEquals(1, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue(), "'triggered' sentence must be turned into a double value as '1.0'.");
        assertEquals("Alert", records.get(0).getRecordType());
    }

    @Test
    void testParseInvalidAndEmptyLines() {
        // ensure avoiding missing/wrong entrances
        assertDoesNotThrow(() -> {
            dataParser.parseAndStore(null, dataStorage);
            dataParser.parseAndStore("", dataStorage);
            dataParser.parseAndStore("   ", dataStorage);
            dataParser.parseAndStore("1, 1714376789050, SystolicPressure", dataStorage); // 3 parça (eksik)
            dataParser.parseAndStore("invalid_id, 1714376789050, ECG, 0.5", dataStorage); // Hatalı ID
        });

        // ensure no invalid data loaded
        List<PatientRecord> records = dataStorage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(0, records.size());
    }

    @Test
    void testExtractValueHelper() {
        assertEquals("35", dataParser.extractValue("Patient ID: 35"));
        assertEquals("ECG", dataParser.extractValue("Label: ECG"));
        assertEquals("120.0", dataParser.extractValue("120.0"));
    }
}
