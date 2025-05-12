/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import Helper.DataTransferHelper;
import Interface.IAdministratorCSVFileManager;
import Models.Administrator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 *
 * @author vishnuprashob
 */
public class AdministratorCSVFileManager implements IAdministratorCSVFileManager {

    private final String filePath = "administrator.csv";

    public AdministratorCSVFileManager() {
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
                        Collections.singletonList(Administrator.getCsvHeader()),
                        StandardOpenOption.APPEND);
                System.out.println("Created new Admin file at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Admin CSV file", e);
        }
    }

    public DataTransferHelper<Boolean, String> saveAdministrator(Administrator newAdmin) {
        List<Administrator> existingAdmins = loadAllAdministrators();
        // Check for duplicate email
        for (Administrator admin : existingAdmins) {
            if (admin.getEmail().equalsIgnoreCase(newAdmin.getEmail())) {
                return new DataTransferHelper<>(false, "Email already exists for administrator!");
            }
        }

        try {
            // If no duplicates, save the administrator
            Files.write(Paths.get(filePath),
                    Collections.singletonList(newAdmin.toCSVRow()),
                    StandardOpenOption.APPEND);
            return new DataTransferHelper<>(true, "Administrator added successfully");
        } catch (IOException ex) {
            Logger.getLogger(AdministratorCSVFileManager.class.getName()).log(Level.SEVERE, null, ex);
            return new DataTransferHelper<>(false, "Failed to save administrator: " + ex.getMessage());
        }
    }

    public List<Administrator> loadAllAdministrators() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            List<Administrator> administrators = new ArrayList<>();

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String[] segment = lines.get(i).split(",");
                if (segment.length == 4) { // id, name, email, password
                    Administrator admin = new Administrator(
                            segment[0], // id
                            segment[1], // name
                            segment[2], // email
                            segment[3] // password
                    );
                    administrators.add(admin);
                }
            }
            return administrators;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load administrators", e);
        }
    }

    public DataTransferHelper<Boolean, String> updateAdministrator(Administrator updatedAdmin) {
        try {
            List<Administrator> admins = loadAllAdministrators();
            boolean found = false;

            for (int i = 0; i < admins.size(); i++) {
                if (admins.get(i).getId().equals(updatedAdmin.getId())) {
                    admins.set(i, updatedAdmin);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return new DataTransferHelper<>(false, "Administrator not found");
            }

            // Rewrite entire file
            Files.write(Paths.get(filePath),
                    Collections.singletonList(Administrator.getCsvHeader()),
                    StandardOpenOption.TRUNCATE_EXISTING);

            for (Administrator admin : admins) {
                Files.write(Paths.get(filePath),
                        Collections.singletonList(admin.toCSVRow()),
                        StandardOpenOption.APPEND);
            }

            return new DataTransferHelper<>(true, "Administrator updated successfully");

        } catch (IOException e) {
            Logger.getLogger(AdministratorCSVFileManager.class.getName()).log(Level.SEVERE, null, e);
            return new DataTransferHelper<>(false, "Failed to update administrator: " + e.getMessage());
        }
    }

    public DataTransferHelper<Boolean, String> deleteAdministrator(String adminId) {
        try {
            List<Administrator> admins = loadAllAdministrators().stream()
                    .filter(admin -> !admin.getId().equals(adminId))
                    .collect(Collectors.toList());

            if (admins.size() == loadAllAdministrators().size()) {
                return new DataTransferHelper<>(false, "Administrator not found");
            }

            // Rewrite entire file
            Files.write(Paths.get(filePath),
                    Collections.singletonList(Administrator.getCsvHeader()),
                    StandardOpenOption.TRUNCATE_EXISTING);

            for (Administrator admin : admins) {
                Files.write(Paths.get(filePath),
                        Collections.singletonList(admin.toCSVRow()),
                        StandardOpenOption.APPEND);
            }

            return new DataTransferHelper<>(true, "Administrator deleted successfully");

        } catch (IOException e) {
            Logger.getLogger(AdministratorCSVFileManager.class.getName()).log(Level.SEVERE, null, e);
            return new DataTransferHelper<>(false, "Failed to delete administrator: " + e.getMessage());
        }
    }

    public Administrator getAdministratorById(String adminId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            return null;
        }

        return loadAllAdministrators().stream()
                .filter(admin -> adminId.equals(admin.getId()))
                .findFirst()
                .orElse(null);
    }

    public Administrator getAdministratorByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return loadAllAdministrators().stream()
                .filter(admin -> email.equalsIgnoreCase(admin.getEmail()))
                .findFirst()
                .orElse(null);
    }

    public DataTransferHelper<Boolean, String> loginCheck(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return new DataTransferHelper<>(false, "Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            return new DataTransferHelper<>(false, "Password cannot be empty");
        }

        Administrator admin = getAdministratorByEmail(email);
        if (admin == null) {
            return new DataTransferHelper<>(false, "Email not found");
        }

        if (!admin.getPassword().equals(password)) {
            return new DataTransferHelper<>(false, "Incorrect password");
        }

        return new DataTransferHelper<>(true, "Login successful");
    }

}
