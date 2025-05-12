package FileManager;

import Models.RepairWork;
import Models.Status;
import Helper.DataTransferHelper;
import Interface.IRepairWorkCSVFileManager;
import Models.JobPart;
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
public class RepairWorkCSVFileManager implements IRepairWorkCSVFileManager {

 private static final String REPAIR_WORK_CSV_HEADER = "JobID,CustomerID,TechnicianID,Equipment,Issue,Status";
    private static final String JOB_PARTS_CSV_HEADER = "JobID,PartID"; // Header for Job-Parts CSV
    private final String repairWorkFilePath = "repairwork.csv";
    private final String jobPartsFilePath = "job_parts.csv"; // New CSV for Job-Part relationships

    public RepairWorkCSVFileManager() {
        initializeFile(repairWorkFilePath, REPAIR_WORK_CSV_HEADER);
        initializeFile(jobPartsFilePath, JOB_PARTS_CSV_HEADER); // Initialize Job-Parts file
    }

 @Override
    public void initializeFile(String filePath, String header) {
        try {
            Path path = Paths.get(filePath);
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
                Files.write(path, Collections.singletonList(header), StandardOpenOption.APPEND);
                System.out.println("Created new file at: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize CSV file: " + filePath, e);
        }
    }


 @Override
    public DataTransferHelper<Boolean, String> saveJobPart(JobPart jobPart) {
        try {
            // Check if the relationship already exists
            if (getPartsByJobId(jobPart.getJobId()).stream()
                .anyMatch(part -> part.getPartId().equals(jobPart.getPartId()))) {
                return new DataTransferHelper<>(false, "Part already linked to this job!");
            }

            // Save the new JobPart
            Files.write(Paths.get(jobPartsFilePath),
                    Collections.singletonList(jobPart.toCSVRow()),
                    StandardOpenOption.APPEND);

            return new DataTransferHelper<>(true, "Part linked to job successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to save JobPart", e);
        }
    }

 
 @Override
    public List<JobPart> getPartsByJobId(String jobId) {
        try {
            return Files.readAllLines(Paths.get(jobPartsFilePath)).stream()
                    .skip(1) // Skip header
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length == 2 && parts[0].equals(jobId))
                    .map(parts -> new JobPart(parts[0], parts[1]))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JobParts", e);
        }
    }


 @Override
    public DataTransferHelper<Boolean, String> removeJobPart(String jobId, String partId) {
        try {
            List<JobPart> jobParts = Files.readAllLines(Paths.get(jobPartsFilePath)).stream()
                    .skip(1)
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length == 2)
                    .map(parts -> new JobPart(parts[0], parts[1]))
                    .filter(jobPart -> !(jobPart.getJobId().equals(jobId) && jobPart.getPartId().equals(partId)))
                    .collect(Collectors.toList());

         
            Files.write(Paths.get(jobPartsFilePath),
                    Collections.singletonList(JOB_PARTS_CSV_HEADER),
                    StandardOpenOption.TRUNCATE_EXISTING);

            for (JobPart jobPart : jobParts) {
                Files.write(Paths.get(jobPartsFilePath),
                        Collections.singletonList(jobPart.toCSVRow()),
                        StandardOpenOption.APPEND);
            }

            return new DataTransferHelper<>(true, "Part removed from job");

        } catch (IOException e) {
            throw new RuntimeException("Failed to remove JobPart", e);
        }
    }

 @Override
    public DataTransferHelper<Boolean, String> saveRepairWork(RepairWork newJob) {
        try {
           
            List<RepairWork> existingJobs = loadAllRepairWorks();
            for (RepairWork job : existingJobs) {
                if (job.getJobId().equals(newJob.getJobId())) {
                    return new DataTransferHelper<>(false, "Job ID already exists!");
                }
            }

            
            Files.write(Paths.get(repairWorkFilePath),
                    Collections.singletonList(newJob.toCSVRow()),
                    StandardOpenOption.APPEND);

            return new DataTransferHelper<>(true, "Repair job saved successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to save repair work", e);
        }
    }

 @Override
    public List<RepairWork> loadAllRepairWorks() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(repairWorkFilePath));
            List<RepairWork> repairWorks = new ArrayList<>();

           
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length == 6) {
                    RepairWork job = new RepairWork(
                            parts[0], 
                            parts[1], 
                            parts[2].equals("null") ? null : parts[2], 
                            parts[3], 
                            parts[4], 
                            Status.valueOf(parts[5])
                    );
                    repairWorks.add(job);
                }
            }
            return repairWorks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load repair works", e);
        }
    }

 @Override
    public List<RepairWork> getJobsByTechnician(String technicianId) {
        return loadAllRepairWorks().stream()
                .filter(job -> technicianId.equals(job.getTechnicianId()))
                .collect(Collectors.toList());
    }

 @Override
    public List<RepairWork> getJobsByCustomer(String customerId) {
        return loadAllRepairWorks().stream()
                .filter(job -> customerId.equals(job.getCustomerId()))
                .collect(Collectors.toList());
    }

 @Override
    public DataTransferHelper<Boolean, String> updateRepairWork(RepairWork updatedJob) {
        try {
            List<RepairWork> jobs = loadAllRepairWorks();

            boolean found = false;
            for (int i = 0; i < jobs.size(); i++) {
                if (jobs.get(i).getJobId().equals(updatedJob.getJobId())) {
                    jobs.set(i, updatedJob);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return new DataTransferHelper<>(false, "Job not found");
            }

            // Rewrite entire file
            Files.write(Paths.get(repairWorkFilePath),
                    Collections.singletonList(REPAIR_WORK_CSV_HEADER),
                    StandardOpenOption.TRUNCATE_EXISTING);

            for (RepairWork job : jobs) {
                Files.write(Paths.get(repairWorkFilePath),
                        Collections.singletonList(job.toCSVRow()),
                        StandardOpenOption.APPEND);
            }

            return new DataTransferHelper<>(true, "Repair job updated successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to update repair work", e);
        }
    }

 @Override
    public DataTransferHelper<Boolean, String> deleteRepairWork(String jobId) {
        try {
            List<RepairWork> jobs = loadAllRepairWorks().stream()
                    .filter(job -> !job.getJobId().equals(jobId))
                    .collect(Collectors.toList());

            if (jobs.size() == loadAllRepairWorks().size()) {
                return new DataTransferHelper<>(false, "Job not found");
            }

            // Rewrite entire file
            Files.write(Paths.get(repairWorkFilePath),
                    Collections.singletonList(REPAIR_WORK_CSV_HEADER),
                    StandardOpenOption.TRUNCATE_EXISTING);

            for (RepairWork job : jobs) {
                Files.write(Paths.get(repairWorkFilePath),
                        Collections.singletonList(job.toCSVRow()),
                        StandardOpenOption.APPEND);
            }

            return new DataTransferHelper<>(true, "Repair job deleted successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete repair work", e);
        }
    }

 @Override
    public RepairWork getRepairWorkById(String jobId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            return null;
        }

        return loadAllRepairWorks().stream()
                .filter(job -> jobId.equals(job.getJobId()))
                .findFirst()
                .orElse(null);
    }
}
