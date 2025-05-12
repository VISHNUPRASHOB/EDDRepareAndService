/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import Helper.DataTransferHelper;
import Helper.TypeValidationHelper;
import Interface.ITechnicianCSVFileManager;
import Models.Technician;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vishnuprashob
 */
public class TechnicianCSVFileManager implements  ITechnicianCSVFileManager{

    private final String filePath = "technicians.csv";

    public TechnicianCSVFileManager() {
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
                        Collections.singletonList(getCsvHeader()),
                        StandardOpenOption.APPEND);
                System.out.println("Created new technician file at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize technician CSV file", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> saveTechnician(Technician newTechnician) {
        try {
            List<Technician> existingTechnicians = loadAllTechnicians();

            // Check for duplicate email
            for (Technician tech : existingTechnicians) {
                if (tech.getEmail().equalsIgnoreCase(newTechnician.getEmail())) {
                    return new DataTransferHelper<>(false, "Email already exists for technician!");
                }
            }

            // If no duplicates, save the technician
            Files.write(Paths.get(filePath),
                    Collections.singletonList(newTechnician.toCSVRow()),
                    StandardOpenOption.APPEND);

            return new DataTransferHelper<>(true, "Technician added successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to save technician", e);
        }
    }

    @Override
    public List<Technician> loadAllTechnicians() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            List<Technician> technicians = new ArrayList<>();

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String[] segment = lines.get(i).split(",");
                if (segment.length == 5) { // id, name, email, expertise, password 
                    Technician tech = new Technician(
                            segment[0], // id
                            segment[1], // name
                            segment[2], // email
                            segment[3], // expertise
                            segment[4]
                    );
                    technicians.add(tech);
                }
            }
            return technicians;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load technicians", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> deleteTechnician(String technicianId) {
        try {
            List<Technician> technicians = loadAllTechnicians();
            List<String> lines = new ArrayList<>();
            lines.add(getCsvHeader()); // Add header back

            boolean found = false;
            for (Technician tech : technicians) {
                if (!tech.getId().equals(technicianId)) {
                    lines.add(tech.toCSVRow());
                } else {
                    found = true;
                }
            }

            if (found) {
                Files.write(Paths.get(filePath), lines);
                return new DataTransferHelper<>(true, "Load Successfully");
                //return true;
            }
            return new DataTransferHelper<>(false, "Loading failed");
            //  return false;

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete technician", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> updateTechnician(Technician updatedTechnician) {
        try {
            List<Technician> technicians = loadAllTechnicians();

            boolean found = false;
            for (int i = 0; i < technicians.size(); i++) {
                if (technicians.get(i).getId().equals(updatedTechnician.getId())) {
                    technicians.set(i, updatedTechnician);
                    found = true;
                    break;
                }
            }

            if (found) {
                List<String> lines = new ArrayList<>();
                lines.add(getCsvHeader());
                for (Technician tech : technicians) {
                    lines.add(tech.toCSVRow());
                }
                Files.write(Paths.get(filePath), lines);
                return new DataTransferHelper<Boolean, String>(true, "Technician updated successfully");
            } else {
                return new DataTransferHelper<Boolean, String>(false, "Error: Technician not found");
                //return "Error: Technician not found";
            }

        } catch (IOException e) {
            return new DataTransferHelper<Boolean, String>(false, "Error updating technician: " + e.getMessage());

        }
    }

    @Override
    public Technician getTechnicianByEmail(String email) {
        List<Technician> technicians = loadAllTechnicians();
        for (Technician tech : technicians) {
            if (tech.getEmail().equalsIgnoreCase(email)) {
                return tech;
            }
        }
        return null;
    }

    @Override
    public Technician getTechnicianById(String id) {
        List<Technician> technicians = loadAllTechnicians();
        for (Technician tech : technicians) {
            if (tech.getId().equals(id)) {
                return tech;
            }
        }
        return null;
    }

    public DataTransferHelper<Boolean, String> technicianLogin(String email, String password) {
     

        String emailValidationError = TypeValidationHelper.emailValidator(email);
        if (emailValidationError != null) {
            return new DataTransferHelper<>(false, emailValidationError);
        }


        String passwordValidationError = TypeValidationHelper.passwordValidator(password);
        if (passwordValidationError != null) {
            return new DataTransferHelper<>(false, passwordValidationError);
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

           
            for (int i = 1; i < lines.size(); i++) {
                String[] fields = lines.get(i).split(",");
                if (fields.length >= 5) { 
                    String storedEmail = fields[2].trim();
                    String storedPassword = fields[4].trim();

                    if (storedEmail.equalsIgnoreCase(email.trim())) {
                        if (storedPassword.equals(password)) {
                            return new DataTransferHelper<>(true, "");
                        } else {
                            return new DataTransferHelper<>(false, "");
                        }
                    }
                }
            }
            return new DataTransferHelper<>(false, "Technician not found");

        } catch (IOException e) {
            return new DataTransferHelper<>(false, "Error accessing technician data: " + e.getMessage());
        }
    }
    
       private String getCsvHeader() {
        return "id,name,email,expertise";
    }

}
