/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import Helper.DataTransferHelper;
import Interface.ICustomerCSVFileManager;
import Models.Customer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vishnuprashob
 */
public class CustomerCSVFileManager implements ICustomerCSVFileManager {

    private final String filePath = "customer.csv";

    public CustomerCSVFileManager() {
        initializeFile();
    }

    @Override
    public void initializeFile() {
        try {
            Path path = Paths.get(filePath);

            // Create parent directories if they don't exist
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            // Create file if it doesn't exist
            if (!Files.exists(path)) {
                Files.createFile(path);
                // Write CSV header
                Files.write(path,
                        Collections.singletonList(Customer.getCsvHeader()),
                        StandardOpenOption.APPEND);
                System.out.println("Created new customer file at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize customer CSV file", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> saveCustomer(Customer newCustomer) {
        try {
            List<Customer> existingCustomers = loadAllCustomers();

            // Check for duplicate email or phone
            for (Customer customer : existingCustomers) {
                if (customer.getEmail().equalsIgnoreCase(newCustomer.getEmail())) {
                    return new DataTransferHelper<>(false, "Email already exists!");
                }
                if (customer.getPhone().equals(newCustomer.getPhone())) {
                    return new DataTransferHelper<>(false, "Phone number already exists!");
                }
            }

            // If no duplicates, save the customer
            Files.write(Paths.get(filePath),
                    Collections.singletonList(newCustomer.toCSVRow()),
                    StandardOpenOption.APPEND);

            return new DataTransferHelper<>(true, "Registration Successful");

        } catch (IOException e) {
            throw new RuntimeException("Failed to save customer", e);
        }
    }

    @Override
    public List<Customer> loadAllCustomers() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            List<Customer> customers = new ArrayList<>();

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String[] segment = lines.get(i).split(",");
                if (segment.length == 6) {
                    Customer customer = new Customer(segment[0],
                            segment[1],
                            segment[2],
                            segment[3],
                            Boolean.parseBoolean(segment[4]),
                            Boolean.parseBoolean(segment[5]));

                    customers.add(customer);
                }
            }
            return customers;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load customers", e);
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public DataTransferHelper<Boolean, String> updateCustomer(String email, String newName, String newPhone) {
        try {
            List<Customer> customers = loadAllCustomers();
            boolean found = false;
            for (int i = 0; i < customers.size(); i++) {
                Customer customer = customers.get(i);
                if (customer.getEmail().equalsIgnoreCase(email)) {
                    // Update customer details
                    customers.set(i, new Customer(customer.getId(), newName, email, newPhone, customer.isRegistered(), customer.isFlagged()));
                    found = true;
                    break;
                }
            }

            if (!found) {
                return new DataTransferHelper<>(false, "Customer with email " + email + " not found.");
            }

            // Rewrite the entire file with the updated customer list
            List<String> lines = customers.stream().map(Customer::toCSVRow).collect(Collectors.toList());
            lines.add(0, Customer.getCsvHeader()); // Add the header
            Files.write(Paths.get(filePath), lines);
            return new DataTransferHelper<>(true, "Customer updated successfully.");

        } catch (IOException e) {
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        List<Customer> customers = loadAllCustomers();
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                return customer;
            }
        }
        return null; // Or throw an exception if you prefer
    }
}
