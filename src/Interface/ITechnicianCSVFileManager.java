/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Helper.DataTransferHelper;
import Models.Technician;
import java.util.List;

/**
 *
 * @author vishnuprashob
 */
public interface ITechnicianCSVFileManager {
     public void initializeFile();
     public DataTransferHelper<Boolean, String> saveTechnician(Technician newTechnician);
     public List<Technician> loadAllTechnicians();
     public DataTransferHelper<Boolean, String> deleteTechnician(String technicianId);
     public DataTransferHelper<Boolean, String> updateTechnician(Technician updatedTechnician);
     public Technician getTechnicianByEmail(String email);
     public Technician getTechnicianById(String id);
     public DataTransferHelper<Boolean, String> technicianLogin(String email, String password);
}
