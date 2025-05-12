/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author vishnuprashob
 */
public enum Status {
    JOB_CREATED("Job Created", 1),
    JOB_ASSIGNED("Job Assigned", 2),
    JOB_INITIATED("Job Initiated", 3),
    JOB_CANCELLED("Job Completed", 4),
    JOB_COMPLETED("Job Completed", 5);

    private final String displayName;
    private final int code;

    // Constructor
    Status(String displayName, int code) {
        this.displayName = displayName;
        this.code = code;
    }

    // Getters
    public String getDisplayName() {
        return displayName;
    }

    public int getCode() {
        return code;
    }

    // Method to convert an integer code to the corresponding Status enum constant
    public static Status fromCode(int code) {
        for (Status status : Status.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
