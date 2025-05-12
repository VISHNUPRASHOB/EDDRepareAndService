/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Helper.DataTransferHelper;
import Models.Customer;
import java.util.List;

/**
 *
 * @author vishnuprashob
 */
public interface ICustomerCSVFileManager {

    public void initializeFile();

    DataTransferHelper<Boolean, String> saveCustomer(Customer newCustomer);

    List<Customer> loadAllCustomers();

    DataTransferHelper<Boolean, String> updateCustomer(String email, String newName, String newPhone);

    Customer getCustomerByEmail(String email);
}
