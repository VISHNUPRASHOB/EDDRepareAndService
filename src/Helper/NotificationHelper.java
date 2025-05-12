/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import FileManager.CustomerCSVFileManager;
import Models.Customer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vishnuprashob
 */
public class NotificationHelper {

    private static final CustomerCSVFileManager csvManager = new CustomerCSVFileManager();

    public DataTransferHelper<Boolean, String> addNotification(String customerEmail, String message) {
        try {
            Path path = Paths.get("customer_notifications.csv");
            Customer customer = csvManager.getCustomerByEmail(customerEmail);
            String notification = customer.getId() + "," + message + "\n";
            Files.write(path, notification.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return new DataTransferHelper<>(true, "Notification added successfully");
        } catch (IOException e) {
            return new DataTransferHelper<>(false, "Failed to add notification: " + e.getMessage());
        }
    }

    public void viewNotification(String loggedInUserEmail) {
        System.out.println("\n=== Notifications ===");
        try {

            Customer customer = csvManager.getCustomerByEmail(loggedInUserEmail);
            if (customer.getId() == null) {
                System.out.println("Customer not found.");
                return;
            }

            Path path = Paths.get("customer_notifications.csv");
            if (Files.exists(path)) {
                List<String> notifications = Files.readAllLines(path)
                        .stream()
                        .filter(line -> {
                            String[] parts = line.split(",", 4);
                            return parts.length >= 1 && parts[0].equals(customer.getId());
                        })
                        .map(line -> {
                            String[] parts = line.split(",", 4);

                            return parts.length > 2 ? parts[1] + " (" + parts[2] + ")" : parts[1];
                        })
                        .collect(Collectors.toList());

                if (notifications.isEmpty()) {
                    System.out.println("No new notifications.");
                } else {
                    System.out.println("You have " + notifications.size() + " notification(s):");
                    for (int i = 0; i < notifications.size(); i++) {
                        System.out.println((i + 1) + ". " + notifications.get(i));
                    }
                }
            } else {
                System.out.println("No notifications found.");
            }
        } catch (IOException e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }
    }
}
