/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import Helper.DataTransferHelper;
import Interface.ISpairPartsCSVFileManager;
import Models.SparePart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author vishnuprashob
 */
public class SpairPartsCSVFileManager implements ISpairPartsCSVFileManager {

    private final String filePath = "spare_parts.csv";

    public SpairPartsCSVFileManager() {
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
                System.out.println("Created new spare parts file at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize spare parts CSV file", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> saveSparePart(SparePart newPart) {
        try {
            List<SparePart> existingParts = loadAllSpareParts();

            // Check for duplicate part ID
            for (SparePart part : existingParts) {
                if (part.getPartId().equalsIgnoreCase(newPart.getPartId())) {
                    return new DataTransferHelper<>(false, "Part ID already exists!");
                }
            }

            // If no duplicates, save the part
            Files.write(Paths.get(filePath),
                    Collections.singletonList(newPart.toCSVRow()),
                    StandardOpenOption.APPEND);

            return new DataTransferHelper<>(true, "Spare part added successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to save spare part", e);
        }
    }

    @Override
    public List<SparePart> loadAllSpareParts() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            List<SparePart> spareParts = new ArrayList<>();

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String[] segment = lines.get(i).split(",");
                if (segment.length == 4) { // partId, name, cost, location
                    SparePart part = new SparePart(
                            segment[0], // partId
                            segment[1], // name
                            Double.parseDouble(segment[2]), // cost
                            segment[3] // location
                    );
                    spareParts.add(part);
                }
            }
            return spareParts;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load spare parts", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> deleteSparePart(String partId) {
        try {
            List<SparePart> parts = loadAllSpareParts();
            List<String> lines = new ArrayList<>();
            lines.add(getCsvHeader()); // Add header back

            boolean found = false;
            for (SparePart part : parts) {
                if (!part.getPartId().equals(partId)) {
                    lines.add(part.toCSVRow());
                } else {
                    found = true;
                }
            }

            if (found) {
                Files.write(Paths.get(filePath), lines);
                return new DataTransferHelper<>(true, "Successfully Deleted");
            }
            return new DataTransferHelper<>(false, "Failed Deleted");

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete spare part", e);
        }
    }

    @Override
    public DataTransferHelper<Boolean, String> updateSparePart(SparePart updatedPart) {
        try {
            List<SparePart> parts = loadAllSpareParts();

            boolean found = false;
            for (int i = 0; i < parts.size(); i++) {
                if (parts.get(i).getPartId().equals(updatedPart.getPartId())) {
                    parts.set(i, updatedPart);
                    found = true;
                    break;
                }
            }

            if (found) {
                List<String> lines = new ArrayList<>();
                lines.add(getCsvHeader());
                for (SparePart part : parts) {
                    lines.add(part.toCSVRow());
                }
                Files.write(Paths.get(filePath), lines);
                //return "";
                return new DataTransferHelper<>(true, "Spare part updated successfully");
            } else {
                return new DataTransferHelper<>(false, "Error: Spare part not found");
                //return "Error: Spare part not found";
            }

        } catch (IOException e) {

            return new DataTransferHelper<>(false, "Error updating spare part: " + e.getMessage());
        }
    }

    @Override
    public SparePart getSparePartById(String partId) {
        if (partId == null || partId.trim().isEmpty()) {
            return null;
        }

        return loadAllSpareParts().stream()
                .filter(part -> partId.equals(part.getPartId()))
                .findFirst()
                .orElse(null);
    }
    
      private String getCsvHeader() {
        return "partId,name,cost,location";
    }
}
