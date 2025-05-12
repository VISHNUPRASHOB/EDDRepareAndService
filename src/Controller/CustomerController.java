
package Controller;

import FileManager.CustomerCSVFileManager;
import Helper.DataTransferHelper;
import Helper.LogHelper;
import Models.Customer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author vishnuprashob
 */
public class CustomerController {

    private final CustomerCSVFileManager csvManager;
    private final LogHelper logHelper;

    public CustomerController() {
        this.csvManager = new CustomerCSVFileManager();
        this.logHelper = new LogHelper();
    }


    public DataTransferHelper<Boolean, String> userRegistration(String name, String email, String phone, boolean flagged) {
       
        if (isEmailExistInCustomer(email)) {
            return new DataTransferHelper<>(false, "Email already exists");
        }

        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id.toString(), name, email, phone, true, flagged);
        return csvManager.saveCustomer(customer);
    }

    public DataTransferHelper<Boolean, String> updateCustomer(String email, String name, String phone, boolean isFlagged) {
        try {
            List<Customer> customers = getAllCustomers();
            boolean found = false;

            for (Customer customer : customers) {
                if (customer.getEmail().equals(email)) {
                    customer.setName(name);
                    customer.setPhone(phone);
                    customer.setFlagged(isFlagged);
                    found = true;
                    break;
                }
            }

            if (found) {
                String result = rewriteAllCustomers(customers);
                return new DataTransferHelper<>(true, result);
            } else {
                return new DataTransferHelper<>(false, "Customer not found");
            }
        } catch (Exception e) {
             logHelper.writeLog("Error updating customer. Exception: " + e.getMessage());
            return new DataTransferHelper<>(false, "Error updating customer: " + e.getMessage());
            
        }
    }

    public DataTransferHelper<Boolean, String> deleteCustomer(String email) {
        try {
            List<Customer> customers = getAllCustomers();
            List<Customer> updatedCustomers = customers.stream()
                    .filter(c -> !c.getEmail().equals(email))
                    .collect(Collectors.toList());

            if (updatedCustomers.size() < customers.size()) {
                String result = rewriteAllCustomers(updatedCustomers);
                return new DataTransferHelper<>(true, result);
            } else {
                return new DataTransferHelper<>(false, "Customer not found");
            }
        } catch (Exception e) {
             logHelper.writeLog("Error deleting customer. Exception: " + e.getMessage());
            return new DataTransferHelper<>(false, "Error deleting customer: " + e.getMessage());
        }
    }

    public List<Customer> getAllCustomers() {
        return csvManager.loadAllCustomers();
    }

    public Customer findCustomerById(String id) {
        return csvManager.loadAllCustomers().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String[] getCustomerDetails(String email) {
        Customer customer = csvManager.loadAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (customer != null) {
            return new String[]{customer.getName(), customer.getEmail(), customer.getPhone(), customer.getFlagged()};
        }
        return null;
    }

    public boolean isEmailExistInCustomer(String email) {
        List<Customer> customers = csvManager.loadAllCustomers();
        return customers.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    public List<String> getAllCustomerEmails() {
        List<Customer> customers = csvManager.loadAllCustomers();
        return customers.stream()
                .map(Customer::getEmail)
                .collect(Collectors.toList());
    }

    private String rewriteAllCustomers(List<Customer> customers) throws IOException {
        Path path = Paths.get(csvManager.getFilePath());

      
        Files.write(path, Collections.singletonList(Customer.getCsvHeader()));

        for (Customer customer : customers) {
            csvManager.saveCustomer(customer);
        }

        return "Customers updated successfully";
    }
}
