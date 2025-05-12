
package Controller;

import FileManager.AdministratorCSVFileManager;
import Helper.DataTransferHelper;
import Models.Administrator;
import java.util.UUID;

/**
 *
 * @author vishnuprashob
 */
public class AdministratorController {

    private final AdministratorCSVFileManager csvManager;

    public AdministratorController() {
        this.csvManager = new AdministratorCSVFileManager();
    }

    public Helper.DataTransferHelper<Boolean, String> userRegistration(String name, String email, String password) {
        UUID id = UUID.randomUUID();
        Administrator admin = new Administrator(id.toString(), name, email, password);
        Helper.DataTransferHelper<Boolean, String> _data = csvManager.saveAdministrator(admin);

        //System.out.println(customer.toString());
        return _data;
    }

    public DataTransferHelper<Boolean, String> adminLoggin(String email, String password) {
        DataTransferHelper<Boolean, String> isLoggedInPayload = csvManager.loginCheck(email, password);
        return isLoggedInPayload.first
                ? new DataTransferHelper<>(true, "Login successful")
                : new DataTransferHelper<>(false, "Login failed");
    }
}
