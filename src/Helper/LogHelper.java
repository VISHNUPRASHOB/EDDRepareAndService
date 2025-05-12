/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author vishnuprashob
 */
public class LogHelper {

    private String LOG_FILE = "eddlog.txt";

    public void writeLog(String message) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            // Get the current timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            logWriter.write("[" + timestamp + "] " + message);
            logWriter.newLine(); // Add a line break after each log entry
        } catch (IOException e) {
            // Handle potential IO errors while writing to the log
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
