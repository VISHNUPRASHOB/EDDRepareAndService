/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Helper.DataTransferHelper;
import Models.Administrator;
import java.util.List;

/**
 *
 * @author vishnuprashob
 */
public interface IAdministratorCSVFileManager {

    public void initializeFile();

    DataTransferHelper<Boolean, String> saveAdministrator(Administrator newAdmin);

    List<Administrator> loadAllAdministrators();

    DataTransferHelper<Boolean, String> updateAdministrator(Administrator updatedAdmin);

    DataTransferHelper<Boolean, String> deleteAdministrator(String adminId);

    Administrator getAdministratorById(String adminId);

    Administrator getAdministratorByEmail(String email);

    DataTransferHelper<Boolean, String> loginCheck(String email, String password);
}
