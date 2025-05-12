/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import Interface.ICsvConvertor;

/**
 *
 * @author vishnuprashob
 */
public class JobPart implements ICsvConvertor {

    private String jobId;   // FK to RepairWork
    private String partId;  // FK to SparePart

    public JobPart(String jobId, String partId) {
        this.jobId = jobId;
        this.partId = partId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getPartId() {
        return partId;
    }

    @Override
    public String toString() {
        return jobId + "," + partId;
    }

    @Override
    public String toCSVRow() {
        return toString();
    }
}
