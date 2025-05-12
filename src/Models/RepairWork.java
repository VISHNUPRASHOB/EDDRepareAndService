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
public class RepairWork implements ICsvConvertor {

    private String jobId;
    private String customerId;     // FK to Customer
    private String technicianId;   // FK to Technician
    private String equipment;
    private String issue;
    private Status status;         // Created, Assessed, Completed
    private String RepairMessage;

    public RepairWork(String jobId, String customerId, String technicianId, String equipment, String issue, Status status) {
        this.jobId = jobId;
        this.customerId = customerId;
        this.technicianId = technicianId;
        this.equipment = equipment;
        this.issue = issue;
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getIssue() {
        return issue;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusString() {
        return status.toString();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String updateIssue(String updateMessage) {
        this.issue = this.issue + " Technition- " + updateMessage;
        return issue;
    }

    @Override
    public String toString() {
        return jobId + "," + customerId + "," + technicianId + "," + equipment + "," + issue + "," + status;
    }

    @Override
    public String toCSVRow() {
        return toString();
    }

    public void setTechnicianId(String techId) {
        this.technicianId = techId;
    }

    public void setCustomerId(String text) {
        this.customerId = text;
    }

    public void setEquipment(String text) {
        this.equipment = text;
    }

    public void setIssue(String text) {
        this.issue = text;
    }
}
