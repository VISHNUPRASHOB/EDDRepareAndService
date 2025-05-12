/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Helper.DataTransferHelper;
import Models.JobPart;
import Models.RepairWork;
import java.util.List;

/**
 *
 * @author vishnuprashob
 */
public interface IRepairWorkCSVFileManager {

    void initializeFile(String filePath, String header);

    DataTransferHelper<Boolean, String> saveJobPart(JobPart jobPart);

    List<JobPart> getPartsByJobId(String jobId);

    DataTransferHelper<Boolean, String> removeJobPart(String jobId, String partId);

    DataTransferHelper<Boolean, String> saveRepairWork(RepairWork newJob);

    List<RepairWork> loadAllRepairWorks();

    List<RepairWork> getJobsByTechnician(String technicianId);

    List<RepairWork> getJobsByCustomer(String customerId);

    DataTransferHelper<Boolean, String> updateRepairWork(RepairWork updatedJob);

    DataTransferHelper<Boolean, String> deleteRepairWork(String jobId);

    RepairWork getRepairWorkById(String jobId);
}
