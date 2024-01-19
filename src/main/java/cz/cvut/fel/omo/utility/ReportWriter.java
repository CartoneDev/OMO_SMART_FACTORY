package cz.cvut.fel.omo.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import lombok.extern.slf4j.XSlf4j;

@XSlf4j(topic = "REPORT")
public class ReportWriter {
    public static void saveReport(StringBuilder stringBuilder, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Convert StringBuilder to String and write it to the file
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            log.error("Error while writing report to file");
        }
    }
}
