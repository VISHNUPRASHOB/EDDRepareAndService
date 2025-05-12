/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import FileManager.TechnicianCSVFileManager;
import Helper.DataTransferHelper;
import Helper.LogHelper;

/**
 *
 * @author vishnuprashob
 */
public class TechnicianController {
    private final TechnicianCSVFileManager csvManager;
    private final LogHelper logHelper;

    public TechnicianController(){
        this.csvManager = new TechnicianCSVFileManager();
        this.logHelper = new LogHelper();
    }
    public DataTransferHelper<Boolean, String> loginTechnician(String email, String password){
        DataTransferHelper<Boolean, String> isLoggedInPayload = csvManager.technicianLogin(email, password);
        return isLoggedInPayload.first
                ? new DataTransferHelper<>(true, "Login successful")
                : new DataTransferHelper<>(false, "Login failed");
    }
    
}
