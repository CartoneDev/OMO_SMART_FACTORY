package cz.cvut.fel.omo.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.XSlf4j;

/**
 * Class for writing reports to files
 */
@XSlf4j(topic = "REPORT")
public class ReportWriter {

    /**
     * Writes the report to the file
     *
     * @param stringBuilder StringBuilder containing the report
     * @param filePath      Path to the file
     */
    public static void saveReport(StringBuilder stringBuilder, String filePath) {
        try {
            // Create directories if they don't exist
            createDirectories(filePath);

            // Create the file (or override if it exists) and open BufferedWriter
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                // Convert StringBuilder to String and write it to the file
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            log.error("Error while writing report to file", e);
        }
    }

    /**
     * Creates directories for the file if they don't exist
     *
     * @param filePath Path to the file
     * @throws IOException If an I/O error occurs
     */
    private static void createDirectories(String filePath) throws IOException {
        Path parentDirectory = Paths.get(filePath).getParent();
        if (parentDirectory != null) {
            try {
                Files.createDirectories(parentDirectory);
            } catch (FileAlreadyExistsException ignored) {
                // Directories already exist, which is okay
            }
        }
    }
}